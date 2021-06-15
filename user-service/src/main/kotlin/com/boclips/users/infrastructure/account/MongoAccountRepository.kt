package com.boclips.users.infrastructure.account

import com.boclips.users.domain.model.account.Account
import com.boclips.users.domain.model.account.AccountRepository
import com.boclips.users.infrastructure.MongoDatabase
import com.mongodb.MongoClient
import com.mongodb.client.MongoCollection
import org.litote.kmongo.findOneById
import org.litote.kmongo.getCollection
import org.litote.kmongo.save

class MongoAccountRepository(
    private val mongoClient: MongoClient
) : AccountRepository {

    private fun collection(): MongoCollection<AccountDocument> =
        mongoClient.getDatabase(MongoDatabase.DB_NAME).getCollection<AccountDocument>("accounts")

    override fun create(account: AccountDocument): Account {
        collection().save(account)

        val createdAccount = collection().findOneById(account._id)
        return createdAccount!!.toAccount()
    }

    override fun findAll(): List<Account> =
        collection().find().map { it.toAccount() }.toList()

}
