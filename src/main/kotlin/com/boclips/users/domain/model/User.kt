package com.boclips.users.domain.model

import com.boclips.users.domain.model.account.Account
import com.boclips.users.domain.model.analytics.MixpanelId
import com.boclips.users.domain.model.identity.Identity

data class User(
    val userId: UserId,
    val account: Account,
    val identity: Identity,
    val analyticsId: MixpanelId,
    val subjects: String
)