package com.boclips.users.domain.model

import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.marketing.MarketingTracking

data class NewTeacher(
    val email: String,
    val password: String,
    val analyticsId: AnalyticsId,
    val referralCode: String,
    val shareCode: String,
    val marketingTracking: MarketingTracking
)
