package com.boclips.users.domain.service

import com.boclips.eventbus.EventBus
import com.boclips.eventbus.events.user.UserCreated
import com.boclips.users.domain.model.Account
import com.boclips.users.domain.model.Platform
import com.boclips.users.domain.model.User
import com.boclips.eventbus.domain.user.User as EventUser

class EventPublishingUserRepository(val userRepository: UserRepository, private val eventBus: EventBus) :
    UserRepository by userRepository {
    override fun save(user: User): User {
        return userRepository.save(user).also(::publishUserCreated)
    }

    override fun save(account: Account): User {
        return userRepository.save(account).also(::publishUserCreated)
    }

    private fun publishUserCreated(createdUser: User) {
        eventBus.publish(
            UserCreated.builder()
                .user(
                    EventUser.builder()
                        .id(createdUser.id.value)
                        .organisationId(
                            when (createdUser.account.platform) {
                                is Platform.BoclipsForTeachers -> null
                                is Platform.ApiCustomer -> createdUser.account.platform.organisationId.value
                            }
                        )
                        .isBoclipsEmployee(createdUser.account.isBoclipsEmployee())
                        .build()
                )
                .build()
        )
    }
}