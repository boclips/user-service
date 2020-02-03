package com.boclips.users.application.commands

import com.boclips.security.utils.UserExtractor
import com.boclips.users.application.UserUpdatesCommandFactory
import com.boclips.users.application.exceptions.NotAuthenticatedException
import com.boclips.users.application.exceptions.PermissionDeniedException
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.UserSessions
import com.boclips.users.domain.model.account.Account
import com.boclips.users.domain.model.account.AccountId
import com.boclips.users.domain.model.account.School
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State
import com.boclips.users.domain.service.MarketingService
import com.boclips.users.domain.service.AccountRepository
import com.boclips.users.domain.service.AccountService
import com.boclips.users.domain.service.UserRepository
import com.boclips.users.domain.service.UserService
import com.boclips.users.domain.service.UserUpdateCommand
import com.boclips.users.domain.service.convertUserToCrmProfile
import com.boclips.users.presentation.requests.UpdateUserRequest
import mu.KLogging
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

// TODO EV: think about how to refactor dependencies
@Component
class UpdateUser(
    private val userService: UserService,
    private val userRepository: UserRepository,
    private val marketingService: MarketingService,
    private val userUpdatesCommandFactory: UserUpdatesCommandFactory,
    private val accountRepository: AccountRepository,
    private val accountService: AccountService,
    private val getOrImportUser: GetOrImportUser
) {
    companion object : KLogging() {
        const val DEFAULT_TRIAL_DAYS_LENGTH = 10L
    }

    operator fun invoke(userId: String, updateUserRequest: UpdateUserRequest): User {
        val authenticatedUser = UserExtractor.getCurrentUser() ?: throw NotAuthenticatedException()
        if (authenticatedUser.id != userId && !authenticatedUser.hasRole(UserRoles.UPDATE_USERS)) throw PermissionDeniedException()

        val updateUserId = UserId(userId)

        logger.info { "User $updateUserId has schoolId ${updateUserRequest.schoolId} and schoolName ${updateUserRequest.schoolName}" }

        val school = findOrCreateSchool(updateUserRequest)

        getOrImportUser(updateUserId).let { user ->
            val updateCommands = buildUpdateCommands(updateUserRequest, school, user)
            updateCommands.let { commands ->
                userRepository.update(user, *commands.toTypedArray())
            }
            updateMarketingService(updateUserId)
        }

        return userService.findUserById(updateUserId)
    }

    private fun buildUpdateCommands(
        updateUserRequest: UpdateUserRequest,
        school: Account<School>?,
        user: User
    ): List<UserUpdateCommand> {
        val updateCommands = userUpdatesCommandFactory.buildCommands(updateUserRequest, school)

        return if (shouldSetAccessExpiresOn(user)) {
            // To calculate the expiry date we decided to round up the date so users get at least the default trial days
            val accessExpiry = ZonedDateTime.now().plusDays(DEFAULT_TRIAL_DAYS_LENGTH + 1).truncatedTo(ChronoUnit.DAYS)

            updateCommands + UserUpdateCommand.ReplaceAccessExpiresOn(accessExpiresOn = accessExpiry)
        } else {
            updateCommands
        }
    }

    private fun shouldSetAccessExpiresOn(user: User): Boolean {
        return (user.teacherPlatformAttributes == null || !user.teacherPlatformAttributes.hasLifetimeAccess) && !user.hasOnboarded()
    }

    private fun findOrCreateSchool(updateUserRequest: UpdateUserRequest): Account<School>? {
        val schoolById = updateUserRequest.schoolId?.let {
            accountService.findOrCreateSchooldiggerSchool(it)
        }

        return schoolById
            ?: updateUserRequest.schoolName?.let { schoolName ->
                findSchoolByName(schoolName, updateUserRequest.country!!)
                    ?: accountRepository.save(
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
    ): Account<School>? {
        return accountRepository.lookupSchools(
            schoolName,
            countryCode
        ).firstOrNull { it.name == schoolName }
            ?.let { accountRepository.findSchoolById(AccountId(it.id)) }
    }

    private fun updateMarketingService(id: UserId) {
        val user = userService.findUserById(id)

        convertUserToCrmProfile(user, UserSessions(Instant.now()))?.let {
            marketingService.updateProfile(listOf(it))
        }

        logger.info { "User $user has updated their information" }
    }
}
