package com.boclips.users.domain.service

import com.boclips.users.domain.model.CrmProfile
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.UserSessions

fun userToCrmProfile(user: User, sessions: UserSessions): CrmProfile {
    return CrmProfile(
        id = UserId(user.id.value),
        activated = user.activated,
        subjects = user.subjects,
        ageRange = user.ageRange,
        firstName = user.firstName,
        lastName = user.lastName,
        email = user.email,
        hasOptedIntoMarketing = user.hasOptedIntoMarketing,
        lastLoggedIn = sessions.lastAccess,
        marketingTracking = user.marketingTracking
    )
}
