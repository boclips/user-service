package com.boclips.users.domain.model

import com.boclips.users.domain.model.analytics.MixpanelId

data class AccountMetadata(val subjects: String, val mixpanelId: MixpanelId)