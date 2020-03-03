package com.boclips.users.infrastructure.contentpackage

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface SpringDataContentPackageMongoRepository : MongoRepository<ContentPackageDocument, String> {
    fun save(contentPackageDocument: ContentPackageDocument): ContentPackageDocument
    fun findById(id: ObjectId): ContentPackageDocument?
}
