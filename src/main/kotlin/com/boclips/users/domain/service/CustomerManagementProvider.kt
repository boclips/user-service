package com.boclips.users.domain.service

import com.boclips.users.domain.model.CrmProfile
import com.boclips.users.domain.model.User

interface CustomerManagementProvider {
    fun update(crmProfiles: List<CrmProfile>)
}