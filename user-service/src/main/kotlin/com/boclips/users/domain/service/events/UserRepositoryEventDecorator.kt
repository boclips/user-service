package com.boclips.users.domain.service.events

import com.boclips.eventbus.EventBus
import com.boclips.eventbus.events.user.UserCreated
import com.boclips.eventbus.events.user.UserUpdated
import com.boclips.users.domain.model.User
import com.boclips.users.domain.service.UserRepository
import com.boclips.users.domain.service.UserUpdate

class UserRepositoryEventDecorator(
    private val userRepository: UserRepository,
    private val eventConverter: EventConverter,
    private val eventBus: EventBus
) :
    UserRepository by userRepository {
    override fun create(user: User): User {
        return userRepository.create(user).also(::publishUserCreated)
    }

    override fun update(user: User, vararg updates: UserUpdate): User {
        return userRepository.update(user, *updates).also(::publishUserUpdated)
    }

    private fun publishUserCreated(user: User) {
        eventBus.publish(
            UserCreated.builder()
                .user(eventConverter.toEventUser(user))
                .build()
        )
    }

    private fun publishUserUpdated(user: User) {
        eventBus.publish(
            UserUpdated.builder()
                .user(eventConverter.toEventUser(user))
                .build()
        )
    }
}
