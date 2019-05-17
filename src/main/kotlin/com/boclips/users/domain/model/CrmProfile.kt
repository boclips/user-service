package com.boclips.users.domain.model

import com.boclips.users.domain.model.analytics.AnalyticsId

class CrmProfile(
    val id: UserId,
    val activated: Boolean,
    val analyticsId: AnalyticsId?,
    val subjects: List<Subject>,
    val ageRange: List<Int>,
    val referralCode: String?,
    val firstName: String,
    val lastName: String,
    val email: String,
    val hasOptedIntoMarketing: Boolean
) {
    override fun toString(): String {
        return "CrmProfile(id=$id)"
    }
}
