package com.boclips.users.infrastructure.account

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test

internal class MongoAccountRepositoryTest : AbstractSpringIntegrationTest() {

    @Test
    fun `can create an account`() {
        val newAccount = AccountDocument(name = "my new account", _id = ObjectId(), products = null)
        val createdAccount = accountRepository.create(newAccount)

        assertThat(createdAccount.id.value).isEqualTo(newAccount._id.toString())
        assertThat(createdAccount.name).isEqualTo(newAccount.name)
    }

    @Test
    fun `can get all accounts`() {
        val firstAccount =
            AccountDocument(name = "my new account", _id = ObjectId(), products = setOf("LTI"))
        val secondAccount = AccountDocument(
            name = "my other new account",
            _id = ObjectId(),
            products = setOf("B2B")
        )
        accountRepository.create(firstAccount)
        accountRepository.create(secondAccount)
        val allAccounts = accountRepository.findAll()

        assertThat(allAccounts).containsExactly(firstAccount.toAccount(), secondAccount.toAccount())
    }
}
