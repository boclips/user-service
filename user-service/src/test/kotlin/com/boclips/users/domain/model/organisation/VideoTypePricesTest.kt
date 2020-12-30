package com.boclips.users.domain.model.organisation

import com.boclips.users.domain.model.access.VideoType.INSTRUCTIONAL
import com.boclips.users.domain.model.access.VideoType.NEWS
import com.boclips.users.domain.model.access.VideoType.STOCK
import com.boclips.users.domain.model.organisation.VideoTypePrices.Price.Companion.DEFAULT_CURRENCY
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class VideoTypePricesTest {

    @Test
    fun `should return prices based on type requested`() {
        val prices = VideoTypePrices(
            instructional = VideoTypePrices.Price(BigDecimal.ONE, DEFAULT_CURRENCY),
            news = VideoTypePrices.Price(BigDecimal.TEN, DEFAULT_CURRENCY),
            stock = VideoTypePrices.Price(BigDecimal.ZERO, DEFAULT_CURRENCY)
        )

        assertThat(prices[INSTRUCTIONAL]).isEqualTo(VideoTypePrices.Price(BigDecimal.ONE, DEFAULT_CURRENCY))
        assertThat(prices[NEWS]).isEqualTo(VideoTypePrices.Price(BigDecimal.TEN, DEFAULT_CURRENCY))
        assertThat(prices[STOCK]).isEqualTo(VideoTypePrices.Price(BigDecimal.ZERO, DEFAULT_CURRENCY))
    }
}
