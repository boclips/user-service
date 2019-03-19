package com.boclips.users.application

import com.boclips.security.utils.User
import com.boclips.security.utils.UserExtractor
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.account.Account
import com.boclips.users.domain.model.account.AccountId
import com.boclips.users.domain.model.account.AccountRepository
import com.boclips.users.domain.model.identity.IdentityId
import com.boclips.users.domain.model.referrals.NewReferral
import com.boclips.users.domain.service.ReferralProvider
import com.boclips.users.domain.service.UserService
import com.boclips.users.presentation.SecurityContextUserNotFoundException
import com.boclips.users.presentation.UserActivationRequest
import org.springframework.stereotype.Component

@Component
class ActivateUser(
    private val userService: UserService,
    private val accountRepository: AccountRepository,
    private val referralProvider: ReferralProvider
) {
    fun activateUser(userActivationRequest: UserActivationRequest?): Account {
        val authenticatedUser: User = UserExtractor.getCurrentUser() ?: throw SecurityContextUserNotFoundException()

        val activatedUser = userService.activate(AccountId(value = authenticatedUser.id))

        if (userActivationRequest?.referralCode != null) {
            val user = userService.findById(IdentityId(value = activatedUser.id.value))

            val referral = NewReferral(
                referralCode = userActivationRequest!!.referralCode!!,
                firstName = user.identity.firstName,
                lastName = user.identity.lastName,
                email = user.identity.email,
                externalIdentifier = user.identity.id.value,
                status = "qualified"
            )
            referralProvider.createReferral(referral)
            accountRepository.markAsReferred(activatedUser.id)
        }

        return activatedUser
    }
}