package com.boclips.users.domain.service.marketing

import com.boclips.users.domain.model.marketing.CrmProfile
import com.boclips.users.domain.model.user.User
import com.boclips.users.domain.model.user.UserId
import com.boclips.users.domain.model.user.UserSessions
import com.boclips.users.domain.model.user.getAges
import com.boclips.users.domain.model.user.getRole
import com.boclips.users.domain.model.user.getSubjects

fun convertUserToCrmProfile(user: User, sessions: UserSessions): CrmProfile? =
    when (user.hasDetailsHidden()) {
        true -> null
        false -> user.getContactDetails()?.let {
            CrmProfile(
                id = UserId(user.id.value),
                activated = user.isActivated(),
                subjects = user.profile.getSubjects(),
                ageRange = user.profile.getAges(),
                firstName = it.firstName,
                lastName = it.lastName,
                email = it.email,
                role = user.profile.getRole(),
                hasOptedIntoMarketing = it.hasOptedIntoMarketing,
                lastLoggedIn = sessions.lastAccess,
                marketingTracking = user.marketingTracking,
                hasLifetimeAccess = user.teacherPlatformAttributes?.hasLifetimeAccess ?: false,
                accessExpiresOn = user.accessExpiresOn?.toInstant()
            )
        }
    }

