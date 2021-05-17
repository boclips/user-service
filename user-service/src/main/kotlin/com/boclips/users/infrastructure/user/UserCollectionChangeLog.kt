package com.boclips.users.infrastructure.user

import com.boclips.users.infrastructure.MongoDatabase.DB_NAME
import com.github.cloudyrock.mongock.ChangeLog
import com.github.cloudyrock.mongock.ChangeSet
import com.mongodb.MongoClient
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import io.changock.migration.api.annotations.NonLockGuarded
import mu.KLogging

@ChangeLog(order = "002")
class UserCollectionChangeLog {
    companion object : KLogging()

    @ChangeSet(order = "001", id = "3", author = "mjanik")
    fun removeCopyLinkButtonFromFeatures(
        @NonLockGuarded mongoClient: MongoClient,
    ) {
        val updateResult = mongoClient.getDatabase(DB_NAME).getCollection("users")
            .updateMany(
                Filters.exists("organisation.features.LTI_COPY_RESOURCE_LINK"),
                Updates.unset("organisation.features.LTI_COPY_RESOURCE_LINK")
            )

        logger.info { "users: unset organisation.features.LTI_COPY_RESOURCE_LINK results: $updateResult" }
    }

    @ChangeSet(order = "002", id = "4", author = "mjanik")
    fun removeCopyLinkButtonFromParentFeatures(
        @NonLockGuarded mongoClient: MongoClient,
    ) {
        val updateResult = mongoClient.getDatabase(DB_NAME).getCollection("users")
            .updateMany(
                Filters.exists("organisation.parent.features.LTI_COPY_RESOURCE_LINK"),
                Updates.unset("organisation.parent.features.LTI_COPY_RESOURCE_LINK")
            )

        logger.info { "users: unset organisation.parent.features.LTI_COPY_RESOURCE_LINK results: $updateResult" }
    }
}
