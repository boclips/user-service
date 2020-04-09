package com.boclips.users.infrastructure.contentpackage

import com.boclips.users.domain.model.contentpackage.ContentPackage
import com.boclips.users.domain.model.contentpackage.ContentPackageId
import com.boclips.users.domain.service.ContentPackageRepository
import com.boclips.users.infrastructure.MongoDatabase
import com.mongodb.MongoClient
import com.mongodb.client.MongoCollection
import org.bson.types.ObjectId
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection
import org.litote.kmongo.save
import org.springframework.stereotype.Repository

@Repository
class MongoContentPackageRepository(
    private val mongoClient: MongoClient
) : ContentPackageRepository {

    private fun collection(): MongoCollection<ContentPackageDocument> {
        return mongoClient.getDatabase(MongoDatabase.DB_NAME).getCollection<ContentPackageDocument>(
            "contentPackages"
        )
    }

    override fun save(contentPackage: ContentPackage): ContentPackage {
        collection().save(
            ContentPackageDocumentConverter.toDocument(
                contentPackage
            )
        )

        return findById(contentPackage.id)!!
    }

    override fun findById(id: ContentPackageId): ContentPackage? {
        return collection().findOne(ContentPackageDocument::_id eq ObjectId(id.value))
            ?.let(ContentPackageDocumentConverter::fromDocument)
    }

    override fun findByName(name: String): ContentPackage? {
        return collection().findOne(ContentPackageDocument::name eq name)
            ?.let(ContentPackageDocumentConverter::fromDocument)
    }
}
