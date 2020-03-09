package com.boclips.users.infrastructure.accessrules

import com.boclips.users.domain.model.contentpackage.AccessRule
import com.boclips.users.domain.model.contentpackage.AccessRuleId
import com.boclips.users.domain.model.contentpackage.CollectionId
import com.boclips.users.domain.model.contentpackage.VideoId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.AccessRuleDocumentFactory
import com.boclips.users.testsupport.factories.AccessRuleFactory
import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.litote.kmongo.KMongo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.mongo.MongoProperties

class MongoAccessRuleRepositoryTest : AbstractSpringIntegrationTest() {
    @Nested
    inner class FindById {
        @Test
        fun `fetches a collection access rule by id and deserializes it to a correct class`() {
            val persistedAccessRule = selectedContentAccessRuleRepository.saveIncludedCollectionsAccessRule(
                name = "Test selected content contract",
                collectionIds = listOf(CollectionId("A"), CollectionId("B"), CollectionId("C"))
            )

            val fetchedAccessRule = accessRuleRepository.findById(persistedAccessRule.id)

            assertThat(fetchedAccessRule).isEqualTo(persistedAccessRule)
        }

        @Test
        fun `fetches a video access rule by id and deserializes it to the correct class`() {
            val persistedAccessRule = selectedContentAccessRuleRepository.saveIncludedVideosAccessRule(
                name = "Test selected content contract",
                videoIds = listOf(VideoId("A"), VideoId("B"), VideoId("C"))
            )

            val fetchedAccessRule = accessRuleRepository.findById(persistedAccessRule.id)

            assertThat(fetchedAccessRule).isEqualTo(persistedAccessRule)
        }

        @Test
        fun `returns null if access rule is not found by id`() {
            assertThat(accessRuleRepository.findById(AccessRuleId("this does not exist"))).isNull()
        }
    }

    @Nested
    inner class FindAllByName {
        @Test
        fun `looks up access rules by name and deserializes them to a correct class`() {
            val accessRuleName = "Name Test"
            val persistedAccessRule = selectedContentAccessRuleRepository.saveIncludedCollectionsAccessRule(
                name = accessRuleName,
                collectionIds = listOf(CollectionId("A"), CollectionId("B"), CollectionId("C"))
            )

            val foundAccessRule = accessRuleRepository.findAllByName(accessRuleName)

            assertThat(foundAccessRule).containsOnly(persistedAccessRule)
        }

        @Test
        fun `returns an empty list if no access rule is found by name`() {
            assertThat(accessRuleRepository.findAllByName("this does not exist")).isEmpty()
        }
    }

    @Nested
    inner class FindAll {
        @Test
        fun `returns all access rules`() {
            val firstCollectionAccessRule = selectedContentAccessRuleRepository.saveIncludedCollectionsAccessRule(
                name = "Hey",
                collectionIds = emptyList()
            )
            val secondCollectionAccessRule = selectedContentAccessRuleRepository.saveIncludedCollectionsAccessRule(
                name = "Ho",
                collectionIds = emptyList()
            )
            val firstVideoAccessRule =
                selectedContentAccessRuleRepository.saveIncludedVideosAccessRule(name = "Yo", videoIds = emptyList())

            val allAccessRules = accessRuleRepository.findAll()

            assertThat(allAccessRules).hasSize(3)
            assertThat(allAccessRules).containsExactlyInAnyOrder(
                firstCollectionAccessRule,
                secondCollectionAccessRule,
                firstVideoAccessRule
            )
        }

        @Test
        fun `returns an empty list if no access rules are found`() {
            assertThat(accessRuleRepository.findAll()).isEmpty()
        }
    }

    // @Autowired
    // lateinit var mongoClient: MongoClient

    @Autowired
    lateinit var mongoProperties: MongoProperties

    @Nested
    inner class WorkingWithNewAndLegacyDocuments {

        @Test
        fun `can read both video documents into domain instances`() {
            // mongoClient.getDatabase("user-service-db").getCollection("contracts", String::class.java).insertOne("""
            //     {
            //         "_id": "${ObjectId().toHexString()}",
            //         "name": "Test Name",
            //         "collectionIds": []
            //     }
            // """.trimIndent())

            val mongoClient = KMongo.createClient(
                MongoClientURI(
                    mongoProperties.determineUri()
                )
            )

            mongoClient.getDatabase("user-service-db").getCollection("contracts", AccessRuleDocument::class.java)
                .insertOne(AccessRuleDocumentFactory.sampleSelectedVideosAccessRuleDocument())



            // mongoTemplate.db.getCollection("contracts", AccessRuleDocument.SelectedVideos::class.java)
            //     .insertOne(AccessRuleDocumentFactory.sampleSelectedVideosAccessRuleDocument())
            accessRuleRepository.save(AccessRuleFactory.sampleIncludedVideosAccessRule())

            val allAccessRules = accessRuleRepository.findAll()

            allAccessRules.map { assertThat(it is AccessRule.IncludedVideos).isTrue() }
        }

        @Test
        fun `can read both collection documents into domain instances`() {
            // mongoTemplate.db.getCollection("contracts", AccessRuleDocument::class.java)
            //     .insertOne(AccessRuleDocumentFactory.sampleSelectedCollectionsAccessRuleDocument())
            accessRuleRepository.save(AccessRuleFactory.sampleIncludedCollectionsAccessRule())

            val allAccessRules = accessRuleRepository.findAll()

            allAccessRules.map { assertThat(it is AccessRule.IncludedCollections).isTrue() }
        }
    }
}
