package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.marketing.MarketingTracking

class MarketingTrackingFactory {
    companion object {
        fun sample(
            utmTerm: String = "",
            utmContent: String = "",
            utmMedium: String = "",
            utmSource: String = "",
            utmCampaign: String = ""
        ): MarketingTracking {
            return MarketingTracking(
                utmTerm = utmTerm,
                utmContent = utmContent,
                utmMedium = utmMedium,
                utmSource = utmSource,
                utmCampaign = utmCampaign
            )
        }
    }
}