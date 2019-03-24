package com.boclips.users.domain.model.account

import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.analytics.AnalyticsId

data class Account(
    val id: UserId,
    val firstName: String,
    val lastName: String,
    val email: String,
    val activated: Boolean,
    val analyticsId: AnalyticsId?,
    val subjects: String?,
    val referralCode: String?
) {
    fun isReferral(): Boolean {
        return !referralCode.isNullOrEmpty()
    }
}