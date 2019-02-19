package com.boclips.users.domain.service

import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.account.Account
import com.boclips.users.domain.model.account.AccountRepository
import com.boclips.users.domain.model.analytics.Event
import com.boclips.users.domain.model.analytics.EventType
import com.boclips.users.domain.model.identity.IdentityId
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

    @Synchronized
    fun registerUserIfNew(id: IdentityId): Account =
        accountRepository.findById(id.value)
            ?: accountRepository
                .save(Account(id = id.value, activated = false))
                .apply {
                    analyticsClient.track(
                        Event(
                            eventType = EventType.ACCOUNT_CREATED,
                            userId = id.value
                        )
                    )
                    logger.info { "Registered new user: $id" }
                }

    fun activate(id: String) = accountRepository.save(Account(id = id, activated = true))

    fun findById(id: IdentityId): User {
        val account = accountRepository.findById(id.value) ?: throw AccountNotFoundException()
        val identity = identityProvider.getUserById(id) ?: throw IdentityNotFoundException()
        val metadata = metadataProvider.getMetadata(id)

        logger.info { "Fetched user ${id.value}" }

        return User(
            account = account,
            identity = identity,
            subjects = metadata.subjects,
            analyticsId = metadata.mixpanelId,
            userId = UserId(id.value)
        )
    }

    fun findAllUsers(): List<User> {
        val identities = identityProvider.getUsers()
        logger.info { "Fetched ${identities.size} users from Keycloak" }

        val allAccounts = accountRepository.findAll(identities.map { it.id.value })
        logger.info { "Fetched ${allAccounts.size} users from database" }

        val allMetadata = metadataProvider.getAllMetadata(identities.map { it.id })
        logger.info { "Fetched ${allMetadata.size} metadata" }

        val allUsers = identities.mapNotNull { identity ->
            val account = allAccounts.find { account -> account.id == identity.id.value }
            val metadata = allMetadata[identity.id]

            when (account) {
                null -> {
                    logger.warn { "Cannot find account for user: ${identity.id.value}. This is probably because the user is new" }
                    null
                }
                else -> User(
                    account = account,
                    identity = identity,
                    subjects = metadata?.subjects,
                    analyticsId = metadata?.mixpanelId,
                    userId = UserId(identity.id.value)
                )
            }
        }

        logger.info { "Return ${allUsers.size} users" }

        return allUsers
    }
}