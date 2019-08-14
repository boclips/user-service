package com.boclips.users.domain.service

import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserSource
import com.boclips.users.domain.model.identity.Identity
import com.boclips.users.domain.model.marketing.MarketingTracking

fun convertIdentityToUser(identity: Identity, userSource: UserSource): User {
    return User(
        id = identity.id,
        activated = false,
        analyticsId = null,
        subjects = emptyList(),
        ages = emptyList(),
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
        associatedTo = userSource
    )
}
