package com.boclips.users.domain.service

import com.boclips.users.domain.model.users.User
import com.boclips.users.domain.model.users.UserRepository
import com.boclips.users.domain.model.events.AnalyticsClient
import com.boclips.users.domain.model.events.Event
import com.boclips.users.domain.model.events.EventType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
        val userRepository: UserRepository,
        val analyticsClient: AnalyticsClient
) {
    @Synchronized // this should be transactional to avoid multi-instance race conditions. Not critical though.
    fun registerUserIfNew(id: String): User =
            userRepository.findById(id)
            ?: userRepository
                    .save(User(id = id, activated = false))
                    .apply { analyticsClient.track(Event(eventType = EventType.BEGIN_ACTIVATION, userId = id)) }
    fun activate(id: String) = userRepository.save(User(id = id, activated = true))
    fun findById(id: String) = userRepository.findById(id)
}