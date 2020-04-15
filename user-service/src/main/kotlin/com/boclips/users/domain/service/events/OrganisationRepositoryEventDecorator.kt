package com.boclips.users.domain.service.events

import com.boclips.eventbus.EventBus
import com.boclips.eventbus.events.organisation.OrganisationUpdated
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.service.OrganisationRepository
import com.boclips.users.domain.service.OrganisationUpdate

class OrganisationRepositoryEventDecorator(
    private val repository: OrganisationRepository,
    private val eventConverter: EventConverter,
    private val eventBus: EventBus
) : OrganisationRepository by repository {

    override fun updateOne(update: OrganisationUpdate): Organisation<*>? {
        val updatedOrganisation = repository.updateOne(update) ?: return null

        val allOrganisations = repository.findOrganisationsByParentId(update.id) + updatedOrganisation

        allOrganisations
            .map(eventConverter::toEventOrganisation)
            .forEach { organisation ->
                eventBus.publish(
                    OrganisationUpdated.builder()
                        .organisation(organisation)
                        .build()
                )
            }


        return updatedOrganisation
    }
}
