package com.boclips.users.application

import com.boclips.security.utils.UserExtractor
import com.boclips.users.application.exceptions.NotAuthenticatedException
import com.boclips.users.application.exceptions.PermissionDeniedException
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.UserSessions
import com.boclips.users.domain.model.referrals.NewReferral
import com.boclips.users.domain.service.MarketingService
import com.boclips.users.domain.service.OrganisationService
import com.boclips.users.domain.service.ReferralProvider
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
    private val referralProvider: ReferralProvider,
    private val marketingService: MarketingService,
    private val userUpdatesConverter: UserUpdatesConverter
) {
    companion object : KLogging()

    operator fun invoke(userId: String, updateUserRequest: UpdateUserRequest): User {
        val authenticatedUser = UserExtractor.getCurrentUser() ?: throw NotAuthenticatedException()
        if (authenticatedUser.id != userId) throw PermissionDeniedException()

        val authenticatedUserId = UserId(authenticatedUser.id)

        val user = userService.findUserById(authenticatedUserId)

        val commands = userUpdatesConverter.convert(updateUserRequest, null)

        userRepository.update(user, *commands.toTypedArray())

        if (!user.hasProfile()) activate(UserId(authenticatedUser.id))

        return userService.findUserById(UserId(authenticatedUser.id))
    }

    private fun activate(id: UserId) {
        val user = userService.findUserById(id)

        if (user.isReferral()) {
            registerReferral(user)
        }

        convertUserToCrmProfile(user, UserSessions(Instant.now()))?.let {
            marketingService.updateProfile(listOf(it))
        }

        logger.info { "User $user has logged in for the first time" }
    }

    private fun registerReferral(activatedUser: User) {
        if (activatedUser.referralCode.isNullOrBlank()) {
            return
        }

        activatedUser.runIfHasContactDetails {
            referralProvider.createReferral(
                NewReferral(
                    referralCode = activatedUser.referralCode,
                    firstName = it.firstName,
                    lastName = it.lastName,
                    email = it.email,
                    externalIdentifier = activatedUser.id.value,
                    status = "qualified"
                )
            )
            logger.info { "Confirmed referral of user ${activatedUser.id}" }
        }
    }
}
