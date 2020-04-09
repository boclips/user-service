package com.boclips.users.infrastructure.accessrules

import com.boclips.users.domain.model.contentpackage.AccessRule
import com.boclips.users.domain.model.contentpackage.AccessRuleId
import com.boclips.users.domain.service.AccessRuleRepository
import com.boclips.users.infrastructure.MongoDatabase
import com.mongodb.MongoClient
import com.mongodb.client.MongoCollection
import org.bson.types.ObjectId
import org.litote.kmongo.`in`
import org.litote.kmongo.eq
import org.litote.kmongo.findOneById
import org.litote.kmongo.getCollection
import org.litote.kmongo.save
import org.springframework.stereotype.Repository

@Repository
class MongoAccessRuleRepository(
    private val mongoClient: MongoClient,
    private val accessRuleDocumentConverter: AccessRuleDocumentConverter
) : AccessRuleRepository {

    private fun collection(): MongoCollection<AccessRuleDocument> {
        return mongoClient.getDatabase(MongoDatabase.DB_NAME).getCollection<AccessRuleDocument>("accessRules")
    }

    override fun findById(id: AccessRuleId): AccessRule? {
        return collection().findOneById(ObjectId(id.value))?.let(accessRuleDocumentConverter::fromDocument)
    }

    override fun findByIds(accessRuleIds: List<AccessRuleId>): List<AccessRule> {
        return collection()
            .find(AccessRuleDocument::_id `in` accessRuleIds.map { ObjectId(it.value) })
            .map(accessRuleDocumentConverter::fromDocument)
            .toList()
    }

    override fun findAll(): List<AccessRule> {
        return collection().find().map(accessRuleDocumentConverter::fromDocument).toList()
    }

    override fun findAllByName(name: String): List<AccessRule> {
        return collection().find(AccessRuleDocument::name eq name)
            .map(accessRuleDocumentConverter::fromDocument)
            .toList()
    }

    override fun <T : AccessRule> save(accessRule: T): T {
        collection().save(
            accessRuleDocumentConverter.toDocument(accessRule)
        )
        @Suppress("UNCHECKED_CAST")
        return findById(accessRule.id)!! as T
    }
}
