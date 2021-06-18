package com.boclips.users.infrastructure.account
import com.boclips.users.infrastructure.MongoDatabase.DB_NAME
import com.github.cloudyrock.mongock.ChangeLog
import com.github.cloudyrock.mongock.ChangeSet
import com.mongodb.MongoClient
import com.mongodb.client.model.Filters
import io.changock.migration.api.annotations.NonLockGuarded
import mu.KLogging
import org.bson.Document
import org.bson.types.ObjectId
import org.litote.kmongo.findOne

@ChangeLog(order = "003")
class AccountCollectionChangeLog {
    companion object : KLogging()

    @ChangeSet(order = "001", id = "2021-06-18T12:10", author = "mjanik,gtatarzyn")
    fun createBoclipsAccount(
        @NonLockGuarded mongoClient: MongoClient,
    ) {
        val database = mongoClient.getDatabase(DB_NAME)
        val boclipsOrg: Document = database.getCollection("organisations").findOne(
            Filters.eq("name", "Boclips")
        )!!

        val boclipsOrganisationId = boclipsOrg["_id"] as ObjectId
        val boclipsAccountDocument = Document().append("name", "Boclips").append("organisations", listOf(boclipsOrganisationId))

        database.createCollection("accounts")
        val insertResult = database.getCollection("accounts")
            .insertOne(boclipsAccountDocument)

        logger.info { "createBoclipsAccount results: $insertResult" }
    }
}
