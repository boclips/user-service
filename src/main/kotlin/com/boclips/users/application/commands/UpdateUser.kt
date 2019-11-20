package com.boclips.users.application.commands

import com.boclips.security.utils.UserExtractor
import com.boclips.users.application.UserUpdatesCommandFactory
import com.boclips.users.application.exceptions.NotAuthenticatedException
import com.boclips.users.application.exceptions.PermissionDeniedException
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.UserSessions
import com.boclips.users.domain.model.organisation.OrganisationAccount
import com.boclips.users.domain.model.organisation.OrganisationAccountId
import com.boclips.users.domain.model.organisation.School
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State
import com.boclips.users.domain.service.MarketingService
import com.boclips.users.domain.service.OrganisationAccountRepository
import com.boclips.users.domain.service.OrganisationService
import com.boclips.users.domain.service.UserRepository
import com.boclips.users.domain.service.UserService
import com.boclips.users.domain.service.convertUserToCrmProfile
import com.boclips.users.presentation.requests.UpdateUserRequest
import mu.KLogging
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class UpdateUser(
    private val userService: UserService,
    private val userRepository: UserRepository,
    private val marketingService: MarketingService,
    private val userUpdatesCommandFactory: UserUpdatesCommandFactory,
    private val organisationAccountRepository: OrganisationAccountRepository,
    private val organisationService: OrganisationService,
    private val getOrImportUser: GetOrImportUser
) {
    companion object : KLogging()

    operator fun invoke(userId: String, updateUserRequest: UpdateUserRequest): User {
        val authenticatedUser = UserExtractor.getCurrentUser() ?: throw NotAuthenticatedException()
        if (authenticatedUser.id != userId) throw PermissionDeniedException()

        val authenticatedUserId = UserId(authenticatedUser.id)

        val school = findOrCreateSchool(updateUserRequest)

        getOrImportUser(authenticatedUserId).let { user ->
            userUpdatesCommandFactory.buildCommands(updateUserRequest, school).let { commands ->
                userRepository.update(user, *commands.toTypedArray())
            }
            if (!user.hasProfile()) activate(UserId(authenticatedUser.id))
        }

        return userService.findUserById(UserId(authenticatedUser.id))
    }

    private fun findOrCreateSchool(updateUserRequest: UpdateUserRequest): OrganisationAccount<School>? {
        val schoolById = updateUserRequest.schoolId?.let {
            organisationService.findOrCreateSchooldiggerSchool(it)
        }

        return schoolById
            ?: updateUserRequest.schoolName?.let { schoolName ->
                findSchoolByName(schoolName, updateUserRequest.country!!)
                    ?: organisationAccountRepository.save(
                        School(
                            name = schoolName,
                            country = Country.fromCode(updateUserRequest.country!!),
                            state = updateUserRequest.state?.let { State.fromCode(it) },
                            district = null,
                            externalId = null
                        )
                    )
            }
    }

    private fun findSchoolByName(
        schoolName: String,
        countryCode: String
    ): OrganisationAccount<School>? {
        return organisationAccountRepository.lookupSchools(
            schoolName,
            countryCode
        ).firstOrNull{ it.name == schoolName }
            ?.let { organisationAccountRepository.findSchoolById(OrganisationAccountId(it.id)) }
    }

    private fun activate(id: UserId) {
        val user = userService.findUserById(id)

        convertUserToCrmProfile(user, UserSessions(Instant.now()))?.let {
            marketingService.updateProfile(listOf(it))
        }

        logger.info { "User $user has logged in for the first time" }
    }
}
