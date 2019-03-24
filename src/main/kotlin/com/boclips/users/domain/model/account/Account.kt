package com.boclips.users.domain.model.account

import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.UserId

data class Account(
    val id: UserId,
    val activated: Boolean,
    val analyticsId: AnalyticsId?,
    val subjects: String?,
    val isReferral: Boolean,
    val referralCode: String?
)