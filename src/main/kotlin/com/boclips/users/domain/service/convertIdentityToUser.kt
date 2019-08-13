package com.boclips.users.domain.service

import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.identity.Identity
import com.boclips.users.domain.model.marketing.MarketingTracking
import com.boclips.users.domain.model.organisation.OrganisationId

fun convertIdentityToUser(identity: Identity, organisationId: OrganisationId?): User {
    return User(
        id = identity.id,
        activated = false,
        analyticsId = null,
        subjects = emptyList(),
        ageRange = emptyList(),
        referralCode = null,
        firstName = identity.firstName,
        lastName = identity.lastName,
        email = identity.email,
        hasOptedIntoMarketing = false,
        marketingTracking = MarketingTracking(
            utmCampaign = "",
            utmSource = "",
            utmContent = "",
            utmMedium = "",
            utmTerm = ""
        ),
        organisationId = organisationId
    )
}
