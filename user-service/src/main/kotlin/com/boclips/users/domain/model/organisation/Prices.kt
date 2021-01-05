package com.boclips.users.domain.model.organisation

import com.boclips.users.domain.model.access.VideoType
import java.math.BigDecimal
import java.util.Currency

data class Prices(
    val videoTypePrices: Map<VideoType, Price>,
    //val premiumContentPartnerPrices: ?
) {

    data class Price(val amount: BigDecimal, val currency: Currency)
}
