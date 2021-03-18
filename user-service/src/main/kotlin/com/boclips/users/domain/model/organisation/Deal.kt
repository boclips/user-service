package com.boclips.users.domain.model.organisation

import java.time.ZonedDateTime

data class Deal(
    val contentAccess: ContentAccess? = null,
    val billing: Boolean,
    val accessExpiresOn: ZonedDateTime?,
    val prices: Prices? = null
)
