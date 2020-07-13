package com.boclips.users.domain.service.events

import com.boclips.eventbus.domain.Subject
import com.boclips.eventbus.domain.SubjectId
import com.boclips.eventbus.domain.user.UserProfile
import com.boclips.users.domain.model.organisation.Address
import com.boclips.users.domain.model.organisation.Deal
import com.boclips.users.domain.model.user.User
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.School
import com.boclips.eventbus.domain.user.Address as EventAddress
import com.boclips.eventbus.domain.user.Deal as EventDeal
import com.boclips.eventbus.domain.user.Organisation as EventOrganisation
import com.boclips.eventbus.domain.user.User as EventUser
import com.boclips.users.domain.model.marketing.MarketingTracking
import com.boclips.eventbus.domain.user.MarketingTracking as EventMarketingTracking

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
                .hasOptedIntoMarketing(user.profile?.hasOptedIntoMarketing)
                .marketingTracking(user.marketingTracking?.let(this::toEventMarketingTracking))
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
            .type(organisation.type().name)
            .name(organisation.name)
            .address(toEventAddress(organisation.address))
            .postcode(organisation.address.postcode)
            .countryCode(organisation.address.country?.id)
            .state(organisation.address.state?.id)
            .deal(toEventDeal(organisation.deal))
            .tags(organisation.tags.map { it.name }.toSet())
            .parent(parent)
            .build()
    }

    fun toEventAddress(address: Address): EventAddress {
        return EventAddress.builder()
            .countryCode(address.country?.id)
            .state(address.state?.id)
            .postcode(address.postcode)
            .build()
    }

    fun toEventDeal(deal: Deal): EventDeal {
        return EventDeal.builder()
            .billing(deal.billing)
            .expiresAt(deal.accessExpiresOn)
            .build()
    }

    private fun parentOrganisation(organisationDetails: Organisation): EventOrganisation? {
        return (organisationDetails as? School?)
            ?.district
            ?.let(this::toEventOrganisation)
    }

    private fun toEventMarketingTracking(marketingTrackingDetails: MarketingTracking): EventMarketingTracking {
        return EventMarketingTracking.builder()
                .utmCampaign(marketingTrackingDetails.utmCampaign)
                .utmSource(marketingTrackingDetails.utmSource)
                .utmMedium(marketingTrackingDetails.utmMedium)
                .utmTerm(marketingTrackingDetails.utmTerm)
                .utmContent(marketingTrackingDetails.utmContent)
                .build()
    }
}
