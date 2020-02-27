package com.boclips.users.domain.service.events

import com.boclips.eventbus.EventBus
import com.boclips.eventbus.events.user.UserUpdated
import com.boclips.users.domain.model.account.Organisation
import com.boclips.users.domain.service.AccountRepository
import com.boclips.users.domain.service.AccountUpdate
import com.boclips.users.domain.service.UserRepository

class AccountRepositoryEventDecorator(
    private val repository: AccountRepository,
    private val userRepository: UserRepository,
    private val eventConverter: EventConverter,
    private val eventBus: EventBus
) : AccountRepository by repository {

    override fun update(update: AccountUpdate): Organisation<*>? {
        val updatedOrganisation = repository.update(update) ?: return null

        val childOrganisations = repository.findAccountsByParentId(update.id) + updatedOrganisation

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
