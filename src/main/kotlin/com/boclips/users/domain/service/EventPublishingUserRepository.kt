package com.boclips.users.domain.service

import com.boclips.eventbus.EventBus
import com.boclips.eventbus.events.user.UserCreated
import com.boclips.eventbus.events.user.UserUpdated
import com.boclips.users.domain.model.Account
import com.boclips.users.domain.model.User
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

    override fun update(user: User, vararg updateCommands: UserUpdateCommand): User {
        return userRepository.update(user, *updateCommands).also(::publishUserUpdated)
    }

    private fun publishUserCreated(user: User) {
        eventBus.publish(
            UserCreated.builder()
                .user(user(user))
                .userId(user.id.value)
                .organisation(organisation(user))
                .build()
        )
    }

    private fun publishUserUpdated(user: User) {
        eventBus.publish(
            UserUpdated.builder()
                .user(user(user))
                .userId(user.id.value)
                .organisation(organisation(user))
                .build()
        )
    }

    private fun user(user: User): EventUser {
        return EventUser.builder()
            .id(user.id.value)
            .isBoclipsEmployee(user.account.isBoclipsEmployee())
            .build()
    }

    private fun organisation(user: User): EventOrganisation? {
        val organisationId = user.organisationAccountId ?: return null
        val account = organisationAccountRepository.findOrganisationAccountById(organisationId) ?: return null
        return EventOrganisation.builder()
            .id(organisationId.value)
            .type(account.organisation.type().name)
            .build()
    }
}
