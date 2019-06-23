package com.boclips.users.domain.service

import com.boclips.users.domain.model.CrmProfile

interface CustomerManagementProvider {
    fun update(crmProfiles: List<CrmProfile>)
    fun unsubscribe(crmProfile: CrmProfile
    )
}