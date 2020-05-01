package com.boclips.users.domain.model.organisation

import com.boclips.users.domain.model.access.ContentPackageId
import java.time.ZonedDateTime

data class Deal(
    val contentPackageId: ContentPackageId? = null,
    val accessExpiresOn: ZonedDateTime?
)
