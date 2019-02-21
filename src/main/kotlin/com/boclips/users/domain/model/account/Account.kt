package com.boclips.users.domain.model.account

import com.boclips.users.domain.model.analytics.MixpanelId

data class Account(
    val id: AccountId,
    val activated: Boolean,
    val analyticsId: MixpanelId?,
    val subjects: String?
)