package com.boclips.users.domain.model.organisation

import com.boclips.users.domain.model.access.VideoType.INSTRUCTIONAL
import com.boclips.users.domain.model.access.VideoType.NEWS
import com.boclips.users.domain.model.access.VideoType.STOCK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class VideoTypePricesTest {

    @Test
    fun `should return prices based on type requested`() {
        val prices = VideoTypePrices(
            instructional = BigDecimal.ONE,
            news = BigDecimal.TEN,
            stock = BigDecimal.ZERO
        )

        assertThat(prices[INSTRUCTIONAL]).isEqualTo(BigDecimal.ONE)
        assertThat(prices[NEWS]).isEqualTo(BigDecimal.TEN)
        assertThat(prices[STOCK]).isEqualTo(BigDecimal.ZERO)
    }
}
