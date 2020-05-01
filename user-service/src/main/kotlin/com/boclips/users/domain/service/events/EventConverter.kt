package com.boclips.users.domain.service.events

import com.boclips.eventbus.domain.Subject
import com.boclips.eventbus.domain.SubjectId
import com.boclips.eventbus.domain.user.UserProfile
import com.boclips.users.domain.model.user.User
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.School
import com.boclips.eventbus.domain.user.Organisation as EventOrganisation
import com.boclips.eventbus.domain.user.User as EventUser

class EventConverter {

    fun toEventUser(user: User): EventUser {
        val profile = UserProfile.builder()
            .firstName(user.profile?.firstName)
            .lastName(user.profile?.lastName)
            .subjects(user.profile?.subjects?.map { subject ->
                Subject.builder().id(SubjectId(subject.id.value)).name(subject.name).build()
            }.orEmpty())
            .ages(user.profile?.ages?.toMutableList().orEmpty())
            .school(user.profile?.school?.let(this::toEventOrganisation))
            .role(user.profile?.role)
            .build()

        return EventUser.builder()
            .id(user.id.value)
            .email(user.identity.email)
            .createdAt(user.identity.createdAt)
            .profile(profile)
            .isBoclipsEmployee(user.identity.isBoclipsEmployee())
            .organisation(user.organisation?.let(this::toEventOrganisation))
            .build()
    }

    fun toEventOrganisation(organisation: Organisation): EventOrganisation {
        val parent = parentOrganisation(organisation)
        return EventOrganisation.builder()
            .id(organisation.id.value)
            .accountType(organisation.deal.type.name)
            .type(organisation.type().name)
            .name(organisation.name)
            .postcode(organisation.address.postcode)
            .countryCode(organisation.address.country?.id)
            .state(organisation.address.state?.id)
            .tags(organisation.tags.map { it.name }.toSet())
            .parent(parent)
            .build()
    }

    private fun parentOrganisation(organisationDetails: Organisation): EventOrganisation? {
        return (organisationDetails as? School?)
            ?.district
            ?.let(this::toEventOrganisation)
    }
}
