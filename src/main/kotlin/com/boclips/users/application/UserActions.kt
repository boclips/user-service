package com.boclips.users.application

import com.boclips.security.utils.User
import com.boclips.security.utils.UserExtractor
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.account.Account
import com.boclips.users.domain.model.account.AccountId
import com.boclips.users.domain.model.account.AccountRepository
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.identity.IdentityId
import com.boclips.users.domain.model.referrals.NewReferral
import com.boclips.users.domain.service.IdentityProvider
import com.boclips.users.domain.service.ReferralProvider
import com.boclips.users.domain.service.UserService
import com.boclips.users.presentation.exceptions.SecurityContextUserNotFoundException
import com.boclips.users.presentation.requests.CreateUserRequest
import com.boclips.users.presentation.requests.UserActivationRequest
import org.springframework.stereotype.Component

@Component
class UserActions(
    private val userService: UserService,
    private val identityProvider: IdentityProvider,
    private val accountRepository: AccountRepository,
    private val referralProvider: ReferralProvider
) {
    fun activate(userActivationRequest: UserActivationRequest?): Account {
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

    fun create(createUserRequest: CreateUserRequest): com.boclips.users.domain.model.User {
        val identity = identityProvider.createNewUser(
            firstName = createUserRequest.firstName,
            lastName = createUserRequest.lastName,
            email = createUserRequest.email,
            password = createUserRequest.password
        )

        val account = accountRepository.save(
            Account(
                id = AccountId(identity.id.value),
                activated = false,
                analyticsId = AnalyticsId(value = createUserRequest.analyticsId.orEmpty()),
                subjects = createUserRequest.subjects,
                isReferral = createUserRequest.referralCode.let { true }
            )
        )

        return com.boclips.users.domain.model.User(
            userId = UserId(identity.id.value),
            account = account,
            identity = identity
        )
    }
}