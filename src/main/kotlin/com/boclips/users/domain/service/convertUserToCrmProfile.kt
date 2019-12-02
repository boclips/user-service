package com.boclips.users.domain.service

import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.UserSessions
import com.boclips.users.domain.model.getAges
import com.boclips.users.domain.model.getSubjects
import com.boclips.users.domain.model.marketing.CrmProfile

fun convertUserToCrmProfile(user: User, sessions: UserSessions): CrmProfile? {
    return user.getContactDetails()?.let {
        CrmProfile(
            id = UserId(user.id.value),
            activated = user.hasOrganisationAssociated(),
            subjects = user.profile.getSubjects(),
            ageRange = user.profile.getAges(),
            firstName = it.firstName,
            lastName = it.lastName,
            email = it.email,
            hasOptedIntoMarketing = it.hasOptedIntoMarketing,
            lastLoggedIn = sessions.lastAccess,
            marketingTracking = user.marketingTracking,
            accessExpiresOn = user.accessExpiresOn?.toInstant()
        )
    }
}

