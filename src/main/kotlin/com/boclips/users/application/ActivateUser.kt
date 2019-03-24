package com.boclips.users.application

import com.boclips.security.utils.User
import com.boclips.security.utils.UserExtractor
import com.boclips.users.domain.model.account.Account
import com.boclips.users.domain.model.account.AccountId
import com.boclips.users.domain.model.identity.IdentityId
import com.boclips.users.domain.model.referrals.NewReferral
import com.boclips.users.domain.service.ReferralProvider
import com.boclips.users.domain.service.UserService
import com.boclips.users.presentation.exceptions.SecurityContextUserNotFoundException
import mu.KLogging
import org.springframework.stereotype.Component

@Component
class ActivateUser(
    private val userService: UserService,
    private val referralProvider: ReferralProvider
) {
    companion object : KLogging()

    operator fun invoke(): Account {
        val authenticatedUser: User = UserExtractor.getCurrentUser() ?: throw SecurityContextUserNotFoundException()

        val activatedUser = userService.activate(AccountId(value = authenticatedUser.id))

        if (activatedUser.isReferral) {
            registerReferral(activatedUser)
        }

        logger.info { "Activated user ${activatedUser.id}" }

        return activatedUser
    }

    private fun registerReferral(activatedUser: Account) {
        val user = userService.findById(IdentityId(value = activatedUser.id.value))

        val referral = NewReferral(
            referralCode = activatedUser.referralCode!!,
            firstName = user.identity.firstName,
            lastName = user.identity.lastName,
            email = user.identity.email,
            externalIdentifier = user.identity.id.value,
            status = "qualified"
        )

        referralProvider.createReferral(referral)
        logger.info { "Confirmed referral of user ${activatedUser.id}" }
    }
}