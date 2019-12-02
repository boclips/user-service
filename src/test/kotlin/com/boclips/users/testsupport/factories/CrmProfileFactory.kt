package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.Subject
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.marketing.CrmProfile
import java.time.Instant

class CrmProfileFactory {
    companion object {
        fun sample(
            ageRanges: List<Int> = emptyList(),
            subjects: List<Subject> = emptyList(),
            lastLoggedIn: Instant? = Instant.now(),
            accessExpiry: Instant? = Instant.now()
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
                hasOptedIntoMarketing = true,
                marketingTracking = MarketingTrackingFactory.sample(),
                accessExpiry = accessExpiry
            )
        }
    }
}
