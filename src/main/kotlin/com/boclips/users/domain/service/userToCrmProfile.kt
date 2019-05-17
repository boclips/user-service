package com.boclips.users.domain.service

import com.boclips.users.domain.model.CrmProfile
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId

fun userToCrmProfile(user: User): CrmProfile {
    return CrmProfile(
        id = UserId(user.id.value),
        activated = user.activated,
        analyticsId = user.analyticsId,
        subjects = user.subjects,
        ageRange = user.ageRange,
        referralCode = user.referralCode,
        firstName = user.firstName,
        lastName = user.lastName,
        email = user.email,
        hasOptedIntoMarketing = user.hasOptedIntoMarketing
    )
}
