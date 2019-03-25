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
import com.boclips.users.domain.model.identity.IdentityNotFoundException
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class UserService(
    val accountRepository: AccountRepository,
    val identityProvider: IdentityProvider,
    val analyticsClient: AnalyticsClient,
    val metadataProvider: MetadataProvider
) {
    companion object : KLogging()

    fun activate(userId: UserId): User {
        val account = accountRepository.activate(userId)!!
        return findById(id = account.id)
    }

    fun findById(id: UserId): User {
        val account = accountRepository.findById(UserId(id.value)) ?: throw AccountNotFoundException()
        val identity = identityProvider.getUserById(id) ?: throw IdentityNotFoundException()
        val metadata = metadataProvider.getMetadata(id)

        val analyticsId = account.analyticsId?.let { it } ?: metadata.analyticsId

        logger.info { "Fetched user ${id.value}" }

        return User(
            account = account.copy(
                subjects = metadata.subjects,
                analyticsId = analyticsId
            ),
            identity = identity,
            userId = UserId(id.value)
        )
    }

    fun findAllUsers(): List<User> {
        val identities = identityProvider.getUsers()
        logger.info { "Fetched ${identities.size} users from Keycloak" }

        val allAccounts = accountRepository.findAll(identities.map { UserId(value = it.id.value) })
        logger.info { "Fetched ${allAccounts.size} users from database" }

        val allMetadata = metadataProvider.getAllMetadata(identities.map { it.id })
        logger.info { "Fetched ${allMetadata.size} metadata" }

        val allUsers = identities.mapNotNull { identity ->
            val account = allAccounts.find { account -> account.id.value == identity.id.value }
            val metadata = allMetadata[identity.id]

            when (account) {
                null -> {
                    logger.warn { "Cannot find account for user: ${identity.id.value}. This is probably because the user is new" }
                    null
                }
                else -> User(
                    account = account.copy(
                        subjects = metadata?.subjects,
                        analyticsId = metadata?.analyticsId
                    ),
                    identity = identity,
                    userId = UserId(identity.id.value)
                )
            }
        }

        logger.info { "Return ${allUsers.size} users" }

        return allUsers
    }

    fun createUser(newUser: NewUser): User {
        val identity = identityProvider.createNewUser(
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
                isReferral = newUser.referralCode.isNotEmpty(),
                referralCode = newUser.referralCode
            )
        )

        CreateUser.logger.info { "Created user ${account.id.value}" }

        trackAccountCreatedEvent(newUser.analyticsId)

        return User(
            userId = UserId(identity.id.value),
            account = account,
            identity = identity
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
            logger.info { "Registered new user: $id" }
        }
    }
}