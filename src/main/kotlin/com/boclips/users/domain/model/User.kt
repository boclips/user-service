package com.boclips.users.domain.model

import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.marketing.MarketingTracking

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
    val marketingTracking: MarketingTracking
) {
    fun isReferral(): Boolean {
        return !referralCode.isNullOrEmpty()
    }

    override fun toString(): String {
        return "User(id=$id)"
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (id != other.id) return false

        return true
    }
}