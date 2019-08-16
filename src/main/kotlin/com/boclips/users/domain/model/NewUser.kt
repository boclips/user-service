package com.boclips.users.domain.model

import com.boclips.users.domain.model.analytics.AnalyticsId

data class NewUser(
    val email: String,
    val password: String,
    val analyticsId: AnalyticsId,
    val referralCode: String,
    val utmSource: String,
    val utmContent: String,
    val utmTerm: String,
    val utmMedium: String,
    val utmCampaign: String
)
