package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.account.AccountProduct.API
import com.boclips.users.domain.model.account.AccountProduct.B2B
import com.boclips.users.domain.model.account.AccountProduct.B2T
import com.boclips.users.domain.model.account.AccountProduct.LTI
import com.boclips.users.infrastructure.MongoDatabase.DB_NAME
import com.boclips.users.infrastructure.account.AccountCollectionChangeLog
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.mongodb.client.model.Filters
import org.assertj.core.api.Assertions
import org.bson.Document
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.litote.kmongo.findOne

class AccountCollectionChangeLogTest : AbstractSpringIntegrationTest() {

    private val accountCollectionChangeLog = AccountCollectionChangeLog()

    @Test
    fun `should update product on account`() {
        val account = Document(
            mapOf(
                "_id" to "account-id",
                "organisations" to listOf(
                    ObjectId("5da7202591e54a00013dd2de")
                )
            )
        )
        val account2 = Document(
            mapOf(
                "_id" to "account-id2",
                "organisations" to listOf(
                    ObjectId("5eaafed77a8aaa6c7a328fdc"),
                )
            )
        )

        mongoClient.getDatabase(DB_NAME).getCollection("accounts")
            .insertMany(listOf(account, account2))

        accountCollectionChangeLog.updateProducts(mongoClient)

        val updated = mongoClient.getDatabase(DB_NAME).getCollection("accounts")
            .findOne(Filters.eq("_id", "account-id"))

        val updated2 = mongoClient.getDatabase(DB_NAME).getCollection("accounts")
            .findOne(Filters.eq("_id", "account-id2"))

        Assertions.assertThat(updated!!["products"] as List<*>).containsExactlyInAnyOrder(B2T.toString())
        Assertions.assertThat(updated2!!["products"]as List<*>).containsExactlyInAnyOrder(B2T.toString(), API.toString()
        )
    }
    @Test
    fun `should add account with no organisations`() {
        accountCollectionChangeLog.addAccounts(mongoClient)

        val added = mongoClient.getDatabase(DB_NAME).getCollection("accounts")
            .findOne(Filters.eq("name", "Savvaas English"))

        val added2 = mongoClient.getDatabase(DB_NAME).getCollection("accounts")
            .findOne(Filters.eq("name", "Savas Physics"))

        Assertions.assertThat(added!!["products"]).isEqualTo(listOf(LTI.toString()))
        Assertions.assertThat(added2!!["products"]).isEqualTo(listOf(B2B.toString()))
    }
}
