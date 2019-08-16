package com.boclips.users.domain.model

import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.marketing.MarketingTracking

data class User(
    val id: UserId,
    val activated: Boolean,
    val analyticsId: AnalyticsId?,
    val subjects: List<Subject>,
    val ages: List<Int>,
    val referralCode: String?,
    val firstName: String?,
    val lastName: String?,
    val email: String,
    val hasOptedIntoMarketing: Boolean,
    val marketingTracking: MarketingTracking,
    val associatedTo: UserSource
) {
    fun isReferral(): Boolean {
        return !referralCode.isNullOrEmpty()
    }

    override fun toString(): String {
        return "User(id=$id)"
    }
}
