package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.organisation.Prices
import java.math.BigDecimal
import java.util.Currency

object PriceFactory {

    fun sample(amount: BigDecimal, currency: Currency = Currency.getInstance("USD")) =
        Prices.Price(amount, currency)
}
