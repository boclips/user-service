package com.boclips.users.infrastructure.account

import com.boclips.users.domain.model.account.AccountProduct.API
import com.boclips.users.domain.model.account.AccountProduct.B2B
import com.boclips.users.domain.model.account.AccountProduct.B2T
import com.boclips.users.domain.model.account.AccountProduct.LTI
import com.boclips.users.infrastructure.MongoDatabase.DB_NAME
import com.github.cloudyrock.mongock.ChangeLog
import com.github.cloudyrock.mongock.ChangeSet
import com.mongodb.MongoClient
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
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
            .map { Document().append("name", it.key).append("organisations", listOf(it.value)) }
            .let {
                database.getCollection("accounts")
                    .insertMany(it)
                logger.info { "createAccounts inserted size: ${it.size}" }
            }
    }

    @ChangeSet(order = "003", id = "2021-06-30T16:20", author = "mrose,gtatarzyn")
    fun addAccounts(
        @NonLockGuarded mongoClient: MongoClient,
    ) {
        val newAccounts = mapOf(
            "Savvaas English" to LTI,
            "Savas Physics" to B2B,
            "Macmillan" to B2B,
            "Nelson" to B2B,
            "Edgenuity" to B2B,
            "Newsela" to B2B,
            "Proquest" to B2B,
        )

        val database = mongoClient.getDatabase(DB_NAME)

        newAccounts.forEach { account ->
            database.getCollection("accounts")
                .insertOne(
                    Document(
                        mapOf("name" to account.key, "products" to setOf(account.value.toString()))
                    )
                )
        }
    }

    @ChangeSet(order = "004", id = "2021-06-30T16:30", author = "mrose,gtatarzyn")
    fun updateProducts(
        @NonLockGuarded mongoClient: MongoClient,
    ) {
        val orgsToProducts = mapOf(
            ObjectId("5da7202591e54a00013dd2de") to setOf(B2T),
            ObjectId("5efb822d661eb614059f2183") to setOf(B2T),
            ObjectId("5eb935dfeacf887f47406167") to setOf(API),
            ObjectId("608816e69b1c59fd91e8e5ae") to setOf(API),
            ObjectId("5e3dab2c5459706d3656394a") to setOf(B2T),
            ObjectId("5d9758142414970001df9a7e") to setOf(B2T),
            ObjectId("6054db1ed57a188c7aed7244") to setOf(API),
            ObjectId("5eafe1d9cc6e484b8fa73963") to setOf(B2T),
            ObjectId("5e1bbffd94337500016f6b32") to setOf(B2T),
            ObjectId("5ef36ab7eaee225eedb97b27") to setOf(B2T),
            ObjectId("60c22794fa0af61943b3beb4") to setOf(API),
            ObjectId("5fb55acb4c06b553f3f00928") to setOf(LTI),
            ObjectId("608bed719b1c59fd91b9f60c") to setOf(LTI),
            ObjectId("5db6dd64da2f8e0001154ea6") to setOf(B2B),
            ObjectId("5e7ed7eb414b24488d475def") to setOf(B2T),
            ObjectId("5f4cc2fb9d18803f96440add") to setOf(API),
            ObjectId("60ae32fefa0af61943400f7f") to setOf(API),
            ObjectId("602d2fa6660d0a77c20b018c") to setOf(API),
            ObjectId("60a3dddffa0af61943961474") to setOf(API),
            ObjectId("5f09f8615229722ed18a0899") to setOf(B2T),
            ObjectId("5d7c3303e86a430001a8f45e") to setOf(B2T),
            ObjectId("5d77c235e9a98f00014dad37") to setOf(API),
            ObjectId("5f4772d3f8326eb9535c54a6") to setOf(API),
            ObjectId("5f0dd1606d9b6ea6d1c7c48b") to setOf(API),
            ObjectId("5f44c7c1ce417355f7048988") to setOf(API),
            ObjectId("5f905703a2bed57119f25ab7") to setOf(B2B),
            ObjectId("5ebd700469f27b1467a6cf0d") to setOf(API),
            ObjectId("5f0f482bbeb8dc26c2cc7b8c") to setOf(B2T),
            ObjectId("5dd51fad0cc6a5000147512b") to setOf(API),
            ObjectId("5eaafed77a8aaa6c7a328fdc") to setOf(API, B2T),
            ObjectId("6048dde8b012acc5a45da9be") to setOf(LTI),
            ObjectId("5f9b081d59876e1f909e988b") to setOf(API),
            ObjectId("5da7aff8fb7862000110bd96") to setOf(B2T),
            ObjectId("5db8221c57a5080001420301") to setOf(B2B),
            ObjectId("5d553ad644f0c2bd459234fd") to setOf(LTI),
            ObjectId("5ea02e96da83afa39ecfe4ba") to setOf(API),
            ObjectId("5dc96c0e5bd9a500015e6048") to setOf(B2T),
            ObjectId("5d7a66c491f4a50001fac9d4") to setOf(B2T),
            ObjectId("5d84c9fbcf4c4b0001fb624d") to setOf(B2T),
            ObjectId("5d9e012bd39e8f0001132586") to setOf(B2T),
            ObjectId("5e31cb16670d6058f4c31522") to setOf(B2T),
            ObjectId("60acc511fa0af61943033f86") to setOf(LTI),
            ObjectId("5e3499a4f63cd14c54c64272") to setOf(B2T),
            ObjectId("5eacd38047c1ac58637c79ae") to setOf(B2T),
            ObjectId("5d7ac8ab91f4a50001fac9dd") to setOf(B2T),
            ObjectId("5f3d5eaacc5999a0d84e031c") to setOf(LTI),
            ObjectId("608be9449b1c59fd91b95f35") to setOf(LTI),
            ObjectId("5f5647d933ef634ff8257aca") to setOf(B2B),
            ObjectId("5e4d8c19a43d614a86be4162") to setOf(B2T),
        )

        val database = mongoClient.getDatabase(DB_NAME)

        orgsToProducts.forEach { org ->
            database.getCollection("accounts")
                .updateOne(
                    Filters.eq("organisations", org.key),
                    Updates.set("products", org.value?.map { it -> it.toString() }.toSet())
                )
        }
    }

    @ChangeSet(order = "005", id = "2021-07-01T12:02", author = "gtatarzyn,aglen")
    fun flattenOrganisations(
        @NonLockGuarded mongoClient: MongoClient,
    ) {
        val database = mongoClient.getDatabase(DB_NAME)

        database.getCollection("accounts").find().forEach { acc ->
            (acc["organisations"] as? List<*>)?.let {
                val orgs = it.flatMap { o ->
                    when (o) {
                        is List<*> -> o
                        else -> listOf(o)
                    }
                }

                database.getCollection("accounts")
                    .updateOne(
                        Filters.eq("_id", acc["_id"]),
                        Updates.set("organisations", orgs)
                    )
            }
        }
    }
}
