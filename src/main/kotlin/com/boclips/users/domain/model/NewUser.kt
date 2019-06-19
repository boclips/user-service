package com.boclips.users.domain.model

import com.boclips.users.domain.model.analytics.AnalyticsId

data class NewUser(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val subjects: List<String>,
    val ageRange: List<Int>,
    val analyticsId: AnalyticsId,
    val referralCode: String,
    val hasOptedIntoMarketing: Boolean,
    val utmSource: String,
    val utmContent: String,
    val utmTerm: String,
    val utmMedium: String,
    val utmCampaign: String
)