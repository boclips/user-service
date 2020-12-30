package com.boclips.users.domain.model.organisation

import com.boclips.users.domain.model.access.VideoType
import java.math.BigDecimal
import java.util.Currency

data class VideoTypePrices(
    val instructional: Price?,
    val news: Price?,
    val stock: Price?
) {

    data class Price(val amount: BigDecimal, val currency: Currency) {

        companion object {

            val DEFAULT_CURRENCY = Currency.getInstance("USD")
        }
    }

    operator fun get(videoType: VideoType): Price? = when (videoType) {
        VideoType.INSTRUCTIONAL -> instructional
        VideoType.NEWS -> news
        VideoType.STOCK -> stock
    }
}
