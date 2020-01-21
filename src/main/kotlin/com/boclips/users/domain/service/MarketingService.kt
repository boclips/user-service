package com.boclips.users.domain.service

import com.boclips.users.domain.model.marketing.CrmProfile

interface MarketingService {
    fun updateProfile(crmProfiles: List<CrmProfile>)
    fun updateSubscription(crmProfile: CrmProfile)
}
