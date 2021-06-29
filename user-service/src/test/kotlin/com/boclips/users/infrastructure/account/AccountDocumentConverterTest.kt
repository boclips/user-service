package com.boclips.users.infrastructure.account

import com.boclips.users.domain.model.account.AccountProducts
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test

internal class AccountDocumentConverterTest : AbstractSpringIntegrationTest() {

    @Test
    fun `can convert account document to account`() {
        val accountDocument =
            AccountDocument(name = "my new account", _id = ObjectId(), products = setOf("API", "LTI", "NOT_A_PRODUCT"))
        val convertedAccount = AccountDocumentConverter.toAccount(accountDocument)

        Assertions.assertThat(convertedAccount.name).isEqualTo("my new account")
        Assertions.assertThat(convertedAccount.id).isNotNull
        Assertions.assertThat(convertedAccount.products).isEqualTo(setOf(AccountProducts.API, AccountProducts.LTI))
    }
}
