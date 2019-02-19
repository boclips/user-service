package com.boclips.users.domain.service

import com.boclips.users.domain.model.events.AnalyticsClient
import com.boclips.users.domain.model.events.Event
import com.boclips.users.domain.model.events.EventType
import com.boclips.users.domain.model.identity.IdentityId
import com.boclips.users.domain.model.users.User
import com.boclips.users.domain.model.users.UserRepository
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class UserService(
    val userRepository: UserRepository,
    val analyticsClient: AnalyticsClient
) {

    companion object : KLogging()

    @Synchronized
    fun registerUserIfNew(id: IdentityId): User =
        userRepository.findById(id.value)
            ?: userRepository
                .save(User(id = id.value, activated = false))
                .apply {
                    analyticsClient.track(Event(eventType = EventType.ACCOUNT_CREATED, userId = id.value))
                    logger.info { "Registered new user: $id" }
                }

    fun activate(id: String) = userRepository.save(User(id = id, activated = true))

    fun findById(id: String) = userRepository.findById(id)
}