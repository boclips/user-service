package com.boclips.users.domain.model.account

import com.boclips.users.domain.model.analytics.AnalyticsId

data class Account(
    val id: AccountId,
    val activated: Boolean,
    val analyticsId: AnalyticsId?,
    val subjects: String?
)