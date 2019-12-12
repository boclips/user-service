package com.boclips.users.domain.service.events

import com.boclips.eventbus.domain.Subject
import com.boclips.eventbus.domain.SubjectId
import com.boclips.eventbus.domain.user.Organisation
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.account.Account
import com.boclips.users.domain.model.account.School
import com.boclips.users.domain.service.AccountRepository

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
        val account = accountRepository.findOrganisationAccountById(organisationId) ?: return null
        return toEventOrganisation(account)
    }

    private fun toEventOrganisation(account: Account<*>): Organisation {
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

    private fun parentOrganisation(organisation: com.boclips.users.domain.model.account.Organisation): Organisation? {
        return when (organisation) {
            is School -> organisation.district?.let(this::toEventOrganisation)
            else -> null
        }
    }

}
