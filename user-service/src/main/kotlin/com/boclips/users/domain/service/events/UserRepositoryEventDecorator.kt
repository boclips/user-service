package com.boclips.users.domain.service.events

import com.boclips.eventbus.BoclipsEventListener
import com.boclips.eventbus.EventBus
import com.boclips.eventbus.events.organisation.OrganisationUpdated
import com.boclips.eventbus.events.user.UserCreated
import com.boclips.eventbus.events.user.UserUpdated
import com.boclips.users.domain.model.user.User
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.organisation.OrganisationRepository
import com.boclips.users.domain.model.user.UserRepository
import com.boclips.users.domain.model.user.UserUpdate

class UserRepositoryEventDecorator(
    private val userRepository: UserRepository,
    private val organisationRepository: OrganisationRepository,
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

    @BoclipsEventListener
    fun updateOrganisation(organisationUpdated: OrganisationUpdated) {
        val organisation =
            organisationRepository.findOrganisationById(OrganisationId(organisationUpdated.organisation.id))!!

        userRepository.findAllByOrganisationId(organisation.id).forEach { user ->
            userRepository.update(user, UserUpdate.ReplaceOrganisation(organisation))
        }
    }
}
