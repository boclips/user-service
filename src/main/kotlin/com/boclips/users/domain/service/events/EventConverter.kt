package com.boclips.users.domain.service.events

import com.boclips.eventbus.domain.Subject
import com.boclips.eventbus.domain.SubjectId
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.organisation.School
import com.boclips.users.domain.service.OrganisationRepository
import com.boclips.eventbus.domain.user.Organisation

class EventConverter(
    private val organisationRepository: OrganisationRepository
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
        val organisationId = user.organisationId ?: return null
        val account = organisationRepository.findOrganisationById(organisationId) ?: return null
        return toEventOrganisation(account)
    }

    private fun toEventOrganisation(organisation: com.boclips.users.domain.model.organisation.Organisation<*>): Organisation {
        val parent = parentOrganisation(organisation.details)
        return Organisation.builder()
            .id(organisation.id.value)
            .accountType(organisation.type.name)
            .type(organisation.details.type().name)
            .name(organisation.details.name)
            .postcode(organisation.details.postcode)
            .parent(parent)
            .build()
    }

    private fun parentOrganisation(organisationDetails: com.boclips.users.domain.model.organisation.OrganisationDetails): Organisation? {
        return when (organisationDetails) {
            is School -> organisationDetails.district?.let(this::toEventOrganisation)
            else -> null
        }
    }

}
