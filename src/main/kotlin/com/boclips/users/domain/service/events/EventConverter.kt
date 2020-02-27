package com.boclips.users.domain.service.events

import com.boclips.eventbus.domain.Subject
import com.boclips.eventbus.domain.SubjectId
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.account.School
import com.boclips.users.domain.service.AccountRepository
import com.boclips.eventbus.domain.user.Organisation

class EventConverter(
    private val accountRepository: AccountRepository
) {

    fun toEventUser(user: User): com.boclips.eventbus.domain.user.User {
        return com.boclips.eventbus.domain.user.User.builder()
            .id(user.id.value)
            .email(user.identity.email)
            .firstName(user.profile?.firstName)
            .lastName(user.profile?.lastName)
            .subjects(user.profile?.subjects?.map { subject -> Subject.builder().id(SubjectId(subject.id.value)).name(subject.name).build() }.orEmpty())
            .ages(user.profile?.ages?.toMutableList().orEmpty())
            .isBoclipsEmployee(user.identity.isBoclipsEmployee())
            .organisation(toEventOrganisation(user))
            .build()
    }

    private fun toEventOrganisation(user: User): Organisation? {
        val organisationId = user.organisationAccountId ?: return null
        val account = accountRepository.findAccountById(organisationId) ?: return null
        return toEventOrganisation(account)
    }

    private fun toEventOrganisation(organisation: com.boclips.users.domain.model.account.Organisation<*>): Organisation {
        val parent = parentOrganisation(organisation.organisation)
        return Organisation.builder()
            .id(organisation.id.value)
            .accountType(organisation.type.name)
            .type(organisation.organisation.type().name)
            .name(organisation.organisation.name)
            .postcode(organisation.organisation.postcode)
            .parent(parent)
            .build()
    }

    private fun parentOrganisation(organisationDetails: com.boclips.users.domain.model.account.OrganisationDetails): Organisation? {
        return when (organisationDetails) {
            is School -> organisationDetails.district?.let(this::toEventOrganisation)
            else -> null
        }
    }

}
