package com.boclips.users.domain.service

import com.boclips.eventbus.EventBus
import com.boclips.eventbus.events.user.UserCreated
import com.boclips.users.domain.model.Account
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.organisation.OrganisationAccountId
import com.boclips.eventbus.domain.user.User as EventUser
import com.boclips.eventbus.domain.user.Organisation as EventOrganisation

class EventPublishingUserRepository(
    private val userRepository: UserRepository,
    private val organisationAccountRepository: OrganisationAccountRepository,
    private val eventBus: EventBus
) :
    UserRepository by userRepository {
    override fun save(user: User): User {
        return userRepository.save(user).also(::publishUserCreated)
    }

    override fun save(account: Account): User {
        return userRepository.save(account).also(::publishUserCreated)
    }

    private fun publishUserCreated(user: User) {

        eventBus.publish(
            UserCreated.builder()
                .user(
                    EventUser.builder()
                        .id(user.id.value)
                        .isBoclipsEmployee(user.account.isBoclipsEmployee())
                        .build()
                )
                .organisation(
                    user.organisationAccountId?.let(this::organisation)
                )
                .build()
        )
    }

    private fun organisation(id: OrganisationAccountId): EventOrganisation? {
        val account = organisationAccountRepository.findOrganisationAccountById(id) ?: return null
        return EventOrganisation.builder()
            .id(id.value)
            .type(account.organisation.type().name)
            .build()
    }
}
