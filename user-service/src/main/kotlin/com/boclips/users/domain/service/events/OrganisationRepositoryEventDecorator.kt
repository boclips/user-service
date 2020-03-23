package com.boclips.users.domain.service.events

import com.boclips.eventbus.EventBus
import com.boclips.eventbus.events.user.UserUpdated
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.service.OrganisationRepository
import com.boclips.users.domain.service.OrganisationUpdate
import com.boclips.users.domain.service.UserRepository

class OrganisationRepositoryEventDecorator(
    private val repository: OrganisationRepository,
    private val userRepository: UserRepository,
    private val eventConverter: EventConverter,
    private val eventBus: EventBus
) : OrganisationRepository by repository {

    override fun updateOne(update: OrganisationUpdate): Organisation<*>? {
        val updatedOrganisation = repository.updateOne(update) ?: return null

        val childOrganisations = repository.findOrganisationsByParentId(update.id) + updatedOrganisation

        childOrganisations.forEach { childOrganisation ->
            userRepository.findAllByOrganisationId(childOrganisation.id).forEach { user ->
                eventBus.publish(
                    UserUpdated.builder()
                        .user(eventConverter.toEventUser(user))
                        .build()
                )
            }
        }


        return updatedOrganisation
    }
}
