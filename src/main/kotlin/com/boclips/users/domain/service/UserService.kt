package com.boclips.users.domain.service

import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.account.Account
import com.boclips.users.domain.model.account.AccountId
import com.boclips.users.domain.model.account.AccountNotFoundException
import com.boclips.users.domain.model.account.AccountRepository
import com.boclips.users.domain.model.analytics.Event
import com.boclips.users.domain.model.analytics.EventType
import com.boclips.users.domain.model.identity.IdentityId
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

    //TODO tidy up
    @Synchronized
    fun registerUserIfNew(id: IdentityId): Account =
        accountRepository.findById(AccountId(value = id.value))
            ?: metadataProvider.getMetadata(id).let { metadata ->
                accountRepository
                    .save(
                        Account(
                            id = AccountId(value = id.value),
                            activated = false,
                            subjects = metadata.subjects,
                            analyticsId = metadata.mixpanelId
                        )
                    )
                    .apply {
                        trackAccountCreatedEvent(id)
                    }
            }

    fun activate(id: AccountId) = accountRepository.activate(id) ?: accountRepository.save(
        Account(
            id = id,
            activated = true,
            subjects = null,
            analyticsId = null
        )
    )

    fun findById(id: IdentityId): User {
        val account = accountRepository.findById(AccountId(id.value)) ?: throw AccountNotFoundException()
        val identity = identityProvider.getUserById(id) ?: throw IdentityNotFoundException()
        val metadata = metadataProvider.getMetadata(id)

        logger.info { "Fetched user ${id.value}" }

        return User(
            account = account.copy(
                subjects = metadata.subjects,
                analyticsId = metadata.mixpanelId
            ),
            identity = identity,
            userId = UserId(id.value)
        )
    }

    fun findAllUsers(): List<User> {
        val identities = identityProvider.getUsers()
        logger.info { "Fetched ${identities.size} users from Keycloak" }

        val allAccounts = accountRepository.findAll(identities.map { AccountId(value = it.id.value) })
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
                        analyticsId = metadata?.mixpanelId
                    ),
                    identity = identity,
                    userId = UserId(identity.id.value)
                )
            }
        }

        logger.info { "Return ${allUsers.size} users" }

        return allUsers
    }

    private fun trackAccountCreatedEvent(id: IdentityId) {
        analyticsClient.track(
            Event(
                eventType = EventType.ACCOUNT_CREATED,
                userId = id.value
            )
        )
        logger.info { "Registered new user: $id" }
    }
}