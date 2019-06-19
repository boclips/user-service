package com.boclips.users.domain.model

data class MarketingTracking(
    val utmSource: String,
    val utmContent: String,
    val utmTerm: String,
    val utmMedium: String,
    val utmCampaign: String
)