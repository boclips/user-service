package com.boclips.users.domain.service.events

import com.boclips.eventbus.EventBus
import com.boclips.eventbus.events.user.UserUpdated
import com.boclips.users.domain.model.organisation.OrganisationAccount
import com.boclips.users.domain.service.OrganisationAccountRepository
import com.boclips.users.domain.service.OrganisationAccountUpdate
import com.boclips.users.domain.service.UserRepository

class OrganisationAccountRepositoryEventDecorator(
    private val repository: OrganisationAccountRepository,
    private val userRepository: UserRepository,
    private val eventConverter: EventConverter,
    private val eventBus: EventBus
) : OrganisationAccountRepository by repository {

    override fun update(update: OrganisationAccountUpdate): OrganisationAccount<*>? {
        val updatedOrganisation = repository.update(update) ?: return null

        val childOrganisations = repository.findOrganisationAccountsByParentId(update.id) + updatedOrganisation

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
