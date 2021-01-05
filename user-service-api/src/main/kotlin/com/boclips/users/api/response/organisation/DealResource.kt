package com.boclips.users.api.response.organisation

import java.time.ZonedDateTime

data class DealResource(
    val accessExpiresOn: ZonedDateTime?,
    val contentPackageId: String?,
    val billing: Boolean?,
    val prices: PricesResource?,
) {

    data class PricesResource (
        val videoTypePrices: Map<String, PriceResource>
    )

    data class PriceResource (
        val amount: String,
        val currency: String
    )
}
