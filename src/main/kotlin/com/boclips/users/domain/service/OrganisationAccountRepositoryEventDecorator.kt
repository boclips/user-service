package com.boclips.users.domain.service

import com.boclips.eventbus.EventBus
import com.boclips.eventbus.domain.Subject
import com.boclips.eventbus.domain.SubjectId
import com.boclips.eventbus.domain.user.Organisation
import com.boclips.eventbus.events.user.UserUpdated
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.organisation.OrganisationAccount
import com.boclips.users.domain.model.organisation.School

class OrganisationAccountRepositoryEventDecorator(
    private val repository: OrganisationAccountRepository,
    private val userRepository: UserRepository,
    private val eventBus: EventBus
) : OrganisationAccountRepository by repository {

    override fun update(update: OrganisationAccountUpdate): OrganisationAccount<*>? {
        val updatedOrganisation = repository.update(update) ?: return null

        val childOrganisations = repository.findOrganisationAccountsByParentId(update.id) + updatedOrganisation

        childOrganisations.forEach { childOrganisation ->
            userRepository.findAllByOrganisationId(childOrganisation.id).forEach { user ->
                eventBus.publish(UserUpdated.builder()
                    .user(toEventUser(user))
                    .build())
            }
        }


        return updatedOrganisation
    }

    private fun toEventUser(user: User): com.boclips.eventbus.domain.user.User {
        return com.boclips.eventbus.domain.user.User.builder()
            .id(user.id.value)
            .email(user.account.email)
            .firstName(user.profile?.firstName)
            .lastName(user.profile?.lastName)
            .subjects(user.profile?.subjects?.map { subject -> Subject.builder().id(SubjectId(subject.id.value)).name(subject.name).build() }.orEmpty())
            .ages(user.profile?.ages?.toMutableList().orEmpty())
            .isBoclipsEmployee(user.account.isBoclipsEmployee())
            .organisation(toEventOrganisation(user))
            .build()
    }

    private fun toEventOrganisation(user: User): Organisation? {
        val organisationId = user.organisationAccountId ?: return null
        val account = repository.findOrganisationAccountById(organisationId) ?: return null
        return toEventOrganisation(account)
    }

    private fun toEventOrganisation(account: OrganisationAccount<*>): Organisation {
        val parent = parentOrganisation(account.organisation)
        return Organisation.builder()
            .id(account.id.value)
            .accountType(account.type.name)
            .type(account.organisation.type().name)
            .name(account.organisation.name)
            .postcode(account.organisation.postcode)
            .parent(parent)
            .build()
    }

    private fun parentOrganisation(organisation: com.boclips.users.domain.model.organisation.Organisation): Organisation? {
        return when (organisation) {
            is School -> organisation.district?.let(this::toEventOrganisation)
            else -> null
        }
    }
}
