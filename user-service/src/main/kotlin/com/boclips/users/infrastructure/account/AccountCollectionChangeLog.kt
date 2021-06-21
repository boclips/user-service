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
        database.getCollection("organisations").findOne(
            Filters.eq("name", "Boclips")
        )?.let { org ->
            val boclipsOrganisationId = org["_id"] as ObjectId
            val boclipsAccountDocument =
                Document().append("name", "Boclips").append("organisations", listOf(boclipsOrganisationId))

            val insertResult = database.getCollection("accounts")
                .insertOne(boclipsAccountDocument)

            logger.info { "createBoclipsAccount results: $insertResult" }
        } ?: logger.info { "no boclips org, skipping account creation" }
    }

    @ChangeSet(order = "002", id = "2021-06-21T17:10", author = "mjanik")
    fun createAccounts(
        @NonLockGuarded mongoClient: MongoClient,
    ) {
        val database = mongoClient.getDatabase(DB_NAME)

        val allOrgsWithDomain = database.getCollection("organisations")
            .find(
                Filters.and(
                    Filters.exists("domain"),
                    Filters.ne("domain", null)
                )
            )
            .map { org -> org["name"]!! as String to org["_id"]!! as ObjectId }
            .groupBy(keySelector = { it.first }, valueTransform = { it.second })
            .toMap()

        val allServiceAccountOrgs: Map<String, ObjectId> = database.getCollection("users")
            .find(Filters.regex("username", "service-account"))
            .mapNotNull { it["organisation"] as? Document }
            .map { org -> org["name"]!! as String to org["_id"]!! as ObjectId }
            .toMap()

        val allOrgs = allServiceAccountOrgs + allOrgsWithDomain

        allOrgs
            .filter { it.key != "Boclips" }
            .map { Document().append("name", it.key).append("organisations", it.value) }
            .let {
                database.getCollection("accounts")
                    .insertMany(it)
                logger.info { "createAccounts inserted size: ${it.size}" }
            }
    }
}
