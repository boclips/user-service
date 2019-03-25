package com.boclips.users.domain.service

import com.boclips.users.application.CreateUser
import com.boclips.users.domain.model.NewUser
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.account.Account
import com.boclips.users.domain.model.account.AccountNotFoundException
import com.boclips.users.domain.model.account.AccountRepository
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.analytics.Event
import com.boclips.users.domain.model.analytics.EventType
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class UserService(
    val accountRepository: AccountRepository,
    val identityProvider: IdentityProvider,
    val analyticsClient: AnalyticsClient
) {
    companion object : KLogging()

    fun activate(userId: UserId): User {
        val account = accountRepository.activate(userId)!!
        return findById(id = account.id)
    }

    fun findById(id: UserId): User {
        val account = accountRepository.findById(UserId(id.value)) ?: throw AccountNotFoundException()

        logger.info { "Fetched user ${id.value}" }

        return User(
            account = account,
            userId = UserId(id.value)
        )
    }

    fun findAllUsers(): List<User> {
        val allAccounts = accountRepository.findAll()
        logger.info { "Fetched ${allAccounts.size} users from database" }

        return allAccounts.mapNotNull { account ->
            User(
                account = account,
                userId = UserId(account.id.value)
            )
        }
    }

    fun createUser(newUser: NewUser): User {
        val identity = identityProvider.createUser(
            firstName = newUser.firstName,
            lastName = newUser.lastName,
            email = newUser.email,
            password = newUser.password
        )

        val account = accountRepository.save(
            Account(
                id = UserId(identity.id.value),
                activated = false,
                analyticsId = newUser.analyticsId,
                subjects = newUser.subjects,
                referralCode = newUser.referralCode,
                firstName = newUser.firstName,
                lastName = newUser.lastName,
                email = newUser.email
            )
        )

        CreateUser.logger.info { "Created user ${account.id.value}" }

        trackAccountCreatedEvent(newUser.analyticsId)

        return User(
            userId = UserId(identity.id.value),
            account = account
        )
    }

    private fun trackAccountCreatedEvent(id: AnalyticsId) {
        if (id.value.isNotEmpty()) {
            analyticsClient.track(
                Event(
                    eventType = EventType.ACCOUNT_CREATED,
                    userId = id.value
                )
            )
            logger.info { "Send MixPanel event ACCOUNT_CREATED for MixPanel ID $id" }
        }
    }
}