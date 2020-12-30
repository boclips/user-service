package com.boclips.users.api.response.organisation

import java.time.ZonedDateTime

data class DealResource(
    val accessExpiresOn: ZonedDateTime?,
    val contentPackageId: String?,
    val billing: Boolean?,
    val prices: VideoTypePricesResource?,
) {

    data class VideoTypePricesResource (
        val instructional: String?,
        val news: String?,
        val stock: String?
    )
}
