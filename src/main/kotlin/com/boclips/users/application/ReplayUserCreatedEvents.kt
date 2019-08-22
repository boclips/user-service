package com.boclips.users.application

import com.boclips.eventbus.EventBus
import com.boclips.eventbus.domain.user.User
import com.boclips.eventbus.events.user.UserCreated
import com.boclips.users.domain.model.Platform
import com.boclips.users.infrastructure.user.MongoUserRepository
import org.springframework.stereotype.Component

@Component
class ReplayUserCreatedEvents(val userRepository: MongoUserRepository, val eventBus: EventBus) {

    fun publishAll() {
        userRepository.findAll().forEach { user ->
            eventBus.publish(
                UserCreated.builder()
                    .user(
                        User.builder()
                            .id(user.id.value)
                            .organisationId(
                                when (user.account.platform) {
                                    is Platform.BoclipsForTeachers -> null
                                    is Platform.ApiCustomer -> user.account.platform.organisationId.value
                                }
                            )
                            .isBoclipsEmployee(user.account.isBoclipsEmployee())
                            .build()
                    )
                    .build()
            )
        }
    }
}