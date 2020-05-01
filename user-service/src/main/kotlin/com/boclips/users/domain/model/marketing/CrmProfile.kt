package com.boclips.users.domain.model.marketing

import com.boclips.users.domain.model.subject.Subject
import com.boclips.users.domain.model.user.UserId
import java.time.Instant

class CrmProfile(
    val id: UserId,
    val activated: Boolean,
    val subjects: List<Subject>,
    val ageRange: List<Int>,
    val firstName: String,
    val lastName: String,
    val email: String,
    val role: String,
    val hasOptedIntoMarketing: Boolean,
    val lastLoggedIn: Instant?,
    val marketingTracking: MarketingTracking,
    val accessExpiresOn: Instant?
) {
    override fun toString(): String {
        return "CrmProfile(id=$id)"
    }
}
