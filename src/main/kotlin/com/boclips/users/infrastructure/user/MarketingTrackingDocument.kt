package com.boclips.users.infrastructure.user

data class MarketingTrackingDocument(
    val utmCampaign: String?,
    val utmTerm: String?,
    val utmMedium: String?,
    val utmContent: String?,
    val utmSource: String?
)