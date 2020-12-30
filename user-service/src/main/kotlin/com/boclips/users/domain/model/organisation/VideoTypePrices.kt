package com.boclips.users.domain.model.organisation

import com.boclips.users.domain.model.access.VideoType
import java.math.BigDecimal

data class VideoTypePrices(
    val instructional: BigDecimal?,
    val news: BigDecimal?,
    val stock: BigDecimal?
) {

    operator fun get(videoType: VideoType): BigDecimal? = when (videoType) {
        VideoType.INSTRUCTIONAL -> instructional
        VideoType.NEWS -> news
        VideoType.STOCK -> stock
    }
}
