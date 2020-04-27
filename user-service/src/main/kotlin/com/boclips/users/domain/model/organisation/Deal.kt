package com.boclips.users.domain.model.organisation

import com.boclips.users.domain.model.contentpackage.ContentPackageId
import java.time.ZonedDateTime

data class Deal(
    val contentPackageId: ContentPackageId? = null,
    val accessExpiresOn: ZonedDateTime?,
    val type: DealType
)
