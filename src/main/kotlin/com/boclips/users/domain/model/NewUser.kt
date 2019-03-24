package com.boclips.users.domain.model

import com.boclips.users.domain.model.analytics.AnalyticsId

data class NewUser(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val subjects: String,
    val analyticsId: AnalyticsId,
    val referralCode: String
)