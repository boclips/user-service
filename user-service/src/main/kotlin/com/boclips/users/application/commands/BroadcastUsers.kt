package com.boclips.users.application.commands

import com.boclips.eventbus.EventBus
import com.boclips.eventbus.events.user.UserBroadcastRequested
import com.boclips.users.domain.model.user.UserRepository
import com.boclips.users.domain.service.events.EventConverter
import mu.KLogging
import org.springframework.stereotype.Component

@Component
class BroadcastUsers(
    private val userRepository: UserRepository,
    private val eventBus: EventBus,
    private val eventConverter: EventConverter
) {
    companion object : KLogging()

    operator fun invoke() {
        logger.info { "Broadcasting all users" }

        userRepository.findAll()
            .map(eventConverter::toEventUser)
            .map { user -> UserBroadcastRequested.builder().user(user).build() }
            .forEach { event -> eventBus.publish(event) }

        logger.info { "All users have been broadcast" }
    }
}
