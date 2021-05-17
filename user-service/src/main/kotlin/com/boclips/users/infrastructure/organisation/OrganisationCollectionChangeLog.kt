package com.boclips.users.infrastructure.organisation

import com.boclips.users.infrastructure.MongoDatabase.DB_NAME
import com.github.cloudyrock.mongock.ChangeLog
import com.github.cloudyrock.mongock.ChangeSet
import com.mongodb.MongoClient
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import io.changock.migration.api.annotations.NonLockGuarded
import mu.KLogging

@ChangeLog(order = "001")
class OrganisationCollectionChangeLog {
    companion object : KLogging()

    @ChangeSet(order = "001", id = "1", author = "mjanik")
    fun removeCopyLinkButtonFromFeatures(
        @NonLockGuarded mongoClient: MongoClient,
    ) {
        val updateResult = mongoClient.getDatabase(DB_NAME).getCollection("organisations")
            .updateMany(
                Filters.exists("features.LTI_COPY_RESOURCE_LINK"),
                Updates.unset("features.LTI_COPY_RESOURCE_LINK")
            )

        logger.info { "organisations: unset features.LTI_COPY_RESOURCE_LINK results: $updateResult" }
    }

    @ChangeSet(order = "002", id = "2", author = "mjanik")
    fun removeCopyLinkButtonFromParentFeatures(
        @NonLockGuarded mongoClient: MongoClient,
    ) {
        val updateResult = mongoClient.getDatabase(DB_NAME).getCollection("organisations")
            .updateMany(
                Filters.exists("parent.features.LTI_COPY_RESOURCE_LINK"),
                Updates.unset("parent.features.LTI_COPY_RESOURCE_LINK")
            )

        logger.info { "organisations: unset parent.features.LTI_COPY_RESOURCE_LINK results: $updateResult" }
    }
}
