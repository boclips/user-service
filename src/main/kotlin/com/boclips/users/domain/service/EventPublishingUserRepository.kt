package com.boclips.users.domain.service

import com.boclips.eventbus.EventBus
import com.boclips.eventbus.domain.Subject
import com.boclips.eventbus.domain.SubjectId
import com.boclips.eventbus.events.user.UserCreated
import com.boclips.eventbus.events.user.UserUpdated
import com.boclips.users.domain.model.Account
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationAccount
import com.boclips.users.domain.model.organisation.School
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
                .user(toEventUser(user))
                .build()
        )
    }

    private fun publishUserUpdated(user: User) {
        eventBus.publish(
            UserUpdated.builder()
                .user(toEventUser(user))
                .build()
        )
    }

    private fun toEventUser(user: User): EventUser {
        return EventUser.builder()
            .id(user.id.value)
            .email(user.account.email)
            .firstName(user.profile?.firstName)
            .lastName(user.profile?.lastName)
            .subjects(user.profile?.subjects?.map { subject -> Subject.builder().id(SubjectId(subject.id.value)).name(subject.name).build() }.orEmpty())
            .isBoclipsEmployee(user.account.isBoclipsEmployee())
            .organisation(toEventOrganisation(user))
            .build()
    }

    private fun toEventOrganisation(user: User): EventOrganisation? {
        val organisationId = user.organisationAccountId ?: return null
        val account = organisationAccountRepository.findOrganisationAccountById(organisationId) ?: return null
        return toEventOrganisation(account)
    }

    private fun toEventOrganisation(account: OrganisationAccount<*>): EventOrganisation {
        val parent = parentOrganisation(account.organisation)
        return EventOrganisation.builder()
            .id(account.id.value)
            .type(account.organisation.type().name)
            .name(account.organisation.name)
            .postcode(account.organisation.postcode)
            .parent(parent)
            .build()
    }

    private fun parentOrganisation(organisation: Organisation): EventOrganisation? {
        return when (organisation) {
            is School -> organisation.district?.let(this::toEventOrganisation)
            else -> null
        }
    }
}
