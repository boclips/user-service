package com.boclips.users.domain.model

import com.boclips.users.domain.model.analytics.AnalyticsId

data class User(
    val id: UserId,
    val activated: Boolean,
    val analyticsId: AnalyticsId?,
    val subjects: List<String>,
    val referralCode: String?,
    val firstName: String,
    val lastName: String,
    val email: String,
    val hasOptedIntoMarketing: Boolean
) {
    fun isReferral(): Boolean {
        return !referralCode.isNullOrEmpty()
    }

    override fun toString(): String {
        return "User(id=$id)"
    }
}