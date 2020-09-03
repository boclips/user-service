package com.boclips.users.application.commands

import com.boclips.security.utils.UserExtractor
import com.boclips.users.api.request.user.UpdateUserRequest
import com.boclips.users.application.UserUpdatesCommandFactory
import com.boclips.users.application.exceptions.NotAuthenticatedException
import com.boclips.users.application.exceptions.PermissionDeniedException
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.user.User
import com.boclips.users.domain.model.user.UserId
import com.boclips.users.domain.model.user.UserSessions
import com.boclips.users.domain.model.organisation.Address
import com.boclips.users.domain.model.organisation.Deal
import com.boclips.users.domain.model.organisation.ExternalOrganisationId
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.organisation.School
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State
import com.boclips.users.domain.service.marketing.MarketingService
import com.boclips.users.domain.model.organisation.OrganisationRepository
import com.boclips.users.domain.service.organisation.OrganisationService
import com.boclips.users.domain.model.user.UserRepository
import com.boclips.users.domain.model.user.UserUpdate
import com.boclips.users.domain.service.marketing.convertUserToCrmProfile
import mu.KLogging
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

@Component
class UpdateUser(
    private val userRepository: UserRepository,
    private val marketingService: MarketingService,
    private val userUpdatesCommandFactory: UserUpdatesCommandFactory,
    private val organisationRepository: OrganisationRepository,
    private val organisationService: OrganisationService,
    private val getOrImportUser: GetOrImportUser,
    private val generateShareCode: GenerateTeacherShareCode
) {
    companion object : KLogging() {
        const val DEFAULT_TRIAL_DAYS_LENGTH = 90L
    }

    operator fun invoke(userId: String, updateUserRequest: UpdateUserRequest): User {
        val authenticatedUser = UserExtractor.getCurrentUser() ?: throw NotAuthenticatedException()
        if (authenticatedUser.id != userId && !authenticatedUser.hasRole(UserRoles.UPDATE_USERS)) throw PermissionDeniedException()

        val updateUserId = UserId(userId)

        logger.info { "Updating user $userId" }

        val school = findOrCreateSchool(updateUserRequest)

        return getOrImportUser(updateUserId).let { user ->
            val updateCommands = buildUpdateCommands(updateUserRequest, school, user)
            userRepository.update(user, *updateCommands.toTypedArray())
        }
            .also { user -> updateMarketingService(user) }
    }

    private fun buildUpdateCommands(
        updateUserRequest: UpdateUserRequest,
        school: School?,
        user: User
    ): List<UserUpdate> = userUpdatesCommandFactory.buildCommands(updateUserRequest, school) +
        listOfNotNull(
            takeIf { user.teacherPlatformAttributes?.shareCode == null }?.let {
                UserUpdate.ReplaceShareCode(generateShareCode())
            },
            takeIf { shouldSetAccessExpiresOn(user) }?.let {
                val accessExpiry = calculateAccessExpiryDate()
                UserUpdate.ReplaceAccessExpiresOn(accessExpiresOn = accessExpiry)
            }
        )

    private fun shouldSetAccessExpiresOn(user: User): Boolean {
        return (user.teacherPlatformAttributes == null || !user.teacherPlatformAttributes.hasLifetimeAccess) && !user.hasOnboarded()
    }

    private fun calculateAccessExpiryDate(): ZonedDateTime {
        return ZonedDateTime.now().plusDays(DEFAULT_TRIAL_DAYS_LENGTH + 1).truncatedTo(ChronoUnit.DAYS)
    }

    private fun findOrCreateSchool(updateUserRequest: UpdateUserRequest): School? {
        val schoolById = updateUserRequest.schoolId?.let {
            organisationService.findOrCreateSchooldiggerSchool(ExternalOrganisationId(it))
        }

        return schoolById
            ?: updateUserRequest.schoolName?.let { schoolName ->
                findSchoolByName(schoolName, updateUserRequest.country!!)
                    ?: organisationRepository.save(
                        organisation = School(
                            id = OrganisationId(),
                            name = schoolName,
                            address = Address(
                                country = Country.fromCode(updateUserRequest.country!!),
                                state = updateUserRequest.state?.let { State.fromCode(it) }
                            ),
                            deal = Deal(
                                accessExpiresOn = null,
                                billing = false
                            ),
                            tags = emptySet(),
                            district = null,
                            externalId = null,
                            role = null,
                            domain = null,
                            features = null
                        )
                    )
            }
    }

    private fun findSchoolByName(
        schoolName: String,
        countryCode: String
    ): School? {
        return organisationRepository.lookupSchools(
            schoolName,
            countryCode
        ).firstOrNull { it.name == schoolName }
    }

    private fun updateMarketingService(user: User) {
        convertUserToCrmProfile(
            user,
            UserSessions(Instant.now())
        )?.let {
            marketingService.updateProfile(listOf(it))
        }
    }
}
