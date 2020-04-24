package com.boclips.users.domain.service.events

import com.boclips.eventbus.domain.Subject
import com.boclips.eventbus.domain.SubjectId
import com.boclips.eventbus.domain.user.Organisation
import com.boclips.eventbus.domain.user.UserProfile
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.organisation.School
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

    fun toEventOrganisation(organisation: com.boclips.users.domain.model.organisation.Organisation<*>): Organisation {
        val parent = parentOrganisation(organisation.details)
        return Organisation.builder()
            .id(organisation.id.value)
            .accountType(organisation.type.name)
            .type(organisation.details.type().name)
            .name(organisation.details.name)
            .postcode(organisation.details.postcode)
                .countryCode(organisation.details.country?.id)
                .state(organisation.details.state?.id)
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
