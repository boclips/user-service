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
        val allAccounts = accountRepository.findAll(identities.map { it.id.value })
        val allMetadata = metadataProvider.getAllMetadata(identities.map { it.id })

        return identities.mapNotNull {
            val account = allAccounts.find { account -> account.id == it.id.value }
            val metadata = allMetadata[it.id]

            when {
                account == null -> {
                    logger.warn { "Cannot find account for user: ${it.id.value}. This is probably because the user is new" }
                    null
                }
                metadata == null -> {
                    logger.warn { "Cannot find metadata for user: ${it.id.value}" }
                    null
                }
                else -> User(
                    account = account,
                    identity = it,
                    subjects = metadata.subjects,
                    analyticsId = metadata.mixpanelId,
                    userId = UserId(it.id.value)
                )
            }
        }
    }
}