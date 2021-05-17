package com.boclips.users.infrastructure.organisation

import com.boclips.users.infrastructure.MongoDatabase.DB_NAME
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.mongodb.client.model.Filters
import org.assertj.core.api.Assertions
import org.bson.Document
import org.junit.jupiter.api.Test
import org.litote.kmongo.findOne

class OrganisationCollectionChangeLogTest : AbstractSpringIntegrationTest() {

    private val organisationCollectionChangeLog = OrganisationCollectionChangeLog()

    @Test
    fun `should remove LTI_COPY_LINK_BUTTON feature from orgs`() {
        val organisation = Document(
            mapOf(
                "_id" to "organisation-id",
                "features" to Document(
                    mapOf(
                        "LTI_COPY_RESOURCE_LINK" to false,
                        "ANOTHER_FEATURE" to true
                    )
                )
            )
        )

        mongoClient.getDatabase(DB_NAME).getCollection("organisations")
            .insertOne(organisation)

        organisationCollectionChangeLog.removeCopyLinkButtonFromFeatures(mongoClient)

        val updated = mongoClient.getDatabase(DB_NAME).getCollection("organisations")
            .findOne(Filters.eq("_id", "organisation-id"))

        Assertions.assertThat(updated).isNotNull
        val features: Document? = updated!!["features"]?.let { it as? Document }
        Assertions.assertThat(features!!.containsKey("LTI_COPY_RESOURCE_LINK")).isFalse
        Assertions.assertThat(features["ANOTHER_FEATURE"]).isEqualTo(true)
    }

    @Test
    fun `should remove LTI_COPY_LINK_BUTTON feature from orgs on parent level`() {
        val parentOrg = Document(
            mapOf(
                "_id" to "parent-id",
                "features" to Document(
                    mapOf(
                        "LTI_COPY_RESOURCE_LINK" to true,
                        "ANOTHER_FEATURE" to true
                    )
                )
            )
        )

        val organisation = Document(
            mapOf(
                "_id" to "organisation-id",
                "parent" to parentOrg,
            )
        )

        mongoClient.getDatabase(DB_NAME).getCollection("organisations")
            .insertOne(organisation)

        organisationCollectionChangeLog.removeCopyLinkButtonFromParentFeatures(mongoClient)

        val updated = mongoClient.getDatabase(DB_NAME).getCollection("organisations")
            .findOne(Filters.eq("_id", "organisation-id"))

        Assertions.assertThat(updated!!).isNotNull
        val parentFeatures: Document? = (updated["parent"] as? Document)!!["features"]?.let { it as? Document }
        Assertions.assertThat(parentFeatures!!.containsKey("LTI_COPY_RESOURCE_LINK")).isFalse
        Assertions.assertThat(parentFeatures["ANOTHER_FEATURE"]).isEqualTo(true)
    }
}
