package com.boclips.users.domain.service

import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.identity.Identity
import com.boclips.users.domain.model.marketing.MarketingTracking

fun convertIdentityToUser(identity: Identity): User {
    return User(
        id = identity.id,
        activated = false,
        analyticsId = null,
        subjects = emptyList(),
        ages = emptyList(),
        referralCode = null,
        firstName = null,
        lastName = null,
        email = identity.email,
        hasOptedIntoMarketing = false,
        marketingTracking = MarketingTracking(),
        associatedTo = identity.associatedTo
    )
}
