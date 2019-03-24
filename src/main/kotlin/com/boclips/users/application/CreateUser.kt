package com.boclips.users.application

import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.account.Account
import com.boclips.users.domain.model.account.AccountId
import com.boclips.users.domain.model.account.AccountRepository
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.service.CustomerManagementProvider
import com.boclips.users.domain.service.IdentityProvider
import com.boclips.users.presentation.requests.CreateUserRequest
import mu.KLogging
import org.springframework.stereotype.Component

@Component
class CreateUser(
    private val identityProvider: IdentityProvider,
    private val accountRepository: AccountRepository,
    private val customerManagementProvider: CustomerManagementProvider
) {
    companion object : KLogging()

    operator fun invoke(createUserRequest: CreateUserRequest): User {
        val identity = identityProvider.createNewUser(
            firstName = createUserRequest.firstName!!,
            lastName = createUserRequest.lastName!!,
            email = createUserRequest.email!!,
            password = createUserRequest.password!!
        )

        val account = accountRepository.save(
            Account(
                id = AccountId(identity.id.value),
                activated = false,
                analyticsId = AnalyticsId(value = createUserRequest.analyticsId.orEmpty()),
                subjects = createUserRequest.subjects,
                isReferral = createUserRequest.referralCode?.let { true } ?: false,
                referralCode = createUserRequest.referralCode
            )
        )


        logger.info { "Created user ${account.id.value}" }
        val user = com.boclips.users.domain.model.User(
            userId = UserId(identity.id.value),
            account = account,
            identity = identity
        )

        try {
            customerManagementProvider.update(users = listOf(user))
        } catch (ex: Exception) {
            logger.error { "Could not update user ${user.userId.value} as a contact on HubSpot" }
        }

        return user
    }
}