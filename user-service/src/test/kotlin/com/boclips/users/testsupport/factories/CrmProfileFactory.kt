package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.marketing.CrmProfile
import com.boclips.users.domain.model.subject.Subject
import com.boclips.users.domain.model.user.UserId
import java.time.Instant

class CrmProfileFactory {
    companion object {
        fun sample(
            ageRanges: List<Int> = emptyList(),
            subjects: List<Subject> = emptyList(),
            lastLoggedIn: Instant? = Instant.now(),
            accessExpiresOn: Instant? = Instant.now(),
            role: String = "",
            hasLifetimeAccess: Boolean = false
        ): CrmProfile {
            return CrmProfile(
                id = UserId(value = "some-id"),
                activated = true,
                subjects = subjects,
                ageRange = ageRanges,
                firstName = "",
                lastName = "",
                lastLoggedIn = lastLoggedIn,
                email = "email@internet.com",
                role = role,
                hasOptedIntoMarketing = true,
                hasLifetimeAccess = hasLifetimeAccess,
                marketingTracking = MarketingTrackingFactory.sample(),
                accessExpiresOn = accessExpiresOn
            )
        }
    }
}
