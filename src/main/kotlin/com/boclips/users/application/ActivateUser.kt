package com.boclips.users.application

import com.boclips.security.utils.UserExtractor
import com.boclips.users.application.exceptions.NotAuthenticatedException
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.referrals.NewReferral
import com.boclips.users.domain.service.CustomerManagementProvider
import com.boclips.users.domain.service.ReferralProvider
import com.boclips.users.domain.service.UserService
import mu.KLogging
import org.springframework.stereotype.Component

@Component
class ActivateUser(
    private val userService: UserService,
    private val referralProvider: ReferralProvider,
    private val customerManagementProvider: CustomerManagementProvider
) {
    companion object : KLogging()

    operator fun invoke(): User {
        val authenticatedUser: com.boclips.security.utils.User =
            UserExtractor.getCurrentUser() ?: throw NotAuthenticatedException()

        val activatedUser = userService.activate(UserId(value = authenticatedUser.id))

        if (activatedUser.isReferral()) {
            registerReferral(activatedUser)
        }

        customerManagementProvider.update(listOf(activatedUser))

        logger.info { "Activated user $activatedUser" }

        return activatedUser
    }

    private fun registerReferral(activatedUser: User) {
        val referral = NewReferral(
            referralCode = activatedUser.referralCode!!,
            firstName = activatedUser.firstName,
            lastName = activatedUser.lastName,
            email = activatedUser.email,
            externalIdentifier = activatedUser.id.value,
            status = "qualified"
        )

        referralProvider.createReferral(referral)
        logger.info { "Confirmed referral of user ${activatedUser.id}" }
    }
}