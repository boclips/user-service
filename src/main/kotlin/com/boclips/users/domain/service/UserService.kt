package com.boclips.users.domain.service

import com.boclips.users.domain.model.events.AnalyticsClient
import com.boclips.users.domain.model.events.Event
import com.boclips.users.domain.model.events.EventType
import com.boclips.users.domain.model.identity.IdentityId
import com.boclips.users.domain.model.account.Account
import com.boclips.users.domain.model.account.AccountRepository
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class UserService(
    val accountRepository: AccountRepository,
    val analyticsClient: AnalyticsClient
) {

    companion object : KLogging()

    @Synchronized
    fun registerUserIfNew(id: IdentityId): Account =
        accountRepository.findById(id.value)
            ?: accountRepository
                .save(Account(id = id.value, activated = false))
                .apply {
                    analyticsClient.track(Event(eventType = EventType.ACCOUNT_CREATED, userId = id.value))
                    logger.info { "Registered new user: $id" }
                }

    fun activate(id: String) = accountRepository.save(Account(id = id, activated = true))

    fun findById(id: String) = accountRepository.findById(id)
}