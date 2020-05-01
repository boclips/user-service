package com.boclips.users.domain.service.events

import com.boclips.eventbus.EventBus
import com.boclips.eventbus.events.organisation.OrganisationUpdated
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.organisation.OrganisationRepository
import com.boclips.users.domain.model.organisation.OrganisationUpdate

class OrganisationRepositoryEventDecorator(
    private val repository: OrganisationRepository,
    private val eventConverter: EventConverter,
    private val eventBus: EventBus
) : OrganisationRepository by repository {

    override fun update(id: OrganisationId, vararg updates: OrganisationUpdate): Organisation? {
        val updatedOrganisation = repository.update(id, *updates) ?: return null

        val allOrganisations = repository.findOrganisationsByParentId(id) + updatedOrganisation

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
