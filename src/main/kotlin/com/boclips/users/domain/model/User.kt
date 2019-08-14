package com.boclips.users.domain.model

import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.marketing.MarketingTracking
import com.boclips.users.domain.model.organisation.OrganisationId

data class User(
    val id: UserId,
    val activated: Boolean,
    val analyticsId: AnalyticsId?,
    val subjects: List<Subject>,
    val ageRange: List<Int>,
    val referralCode: String?,
    val firstName: String,
    val lastName: String,
    val email: String,
    val hasOptedIntoMarketing: Boolean,
    val marketingTracking: MarketingTracking,
    val associatedTo: OrganisationId? = null
) {
    fun isReferral(): Boolean {
        return !referralCode.isNullOrEmpty()
    }

    override fun toString(): String {
        return "User(id=$id)"
    }
}
