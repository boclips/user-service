package com.boclips.users.infrastructure.user

data class MarketingTrackingDocument(
    var utmCampaign: String?,
    var utmTerm: String?,
    var utmMedium: String?,
    var utmContent: String?,
    var utmSource: String?
)
