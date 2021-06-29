package com.boclips.users.infrastructure.account

import com.boclips.users.domain.model.account.Account
import com.boclips.users.domain.model.account.AccountId
import com.boclips.users.domain.model.account.AccountProduct
import mu.KLogging

object AccountDocumentConverter : KLogging() {
    fun toAccount(document: AccountDocument): Account = Account(
        id = AccountId(document._id.toHexString()),
        name = document.name,
        products = document.products.orEmpty().mapNotNull { products ->
            try {
                AccountProduct.valueOf(products)
            } catch (_: IllegalArgumentException) {
                throw (UnknownProductException("Unrecognised products [$products] on account ${document._id}"))
            }
        }.toSet()
    )
}
