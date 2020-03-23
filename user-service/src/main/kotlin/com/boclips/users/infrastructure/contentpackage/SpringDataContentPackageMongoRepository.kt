package com.boclips.users.infrastructure.contentpackage

import org.springframework.data.mongodb.repository.MongoRepository

interface SpringDataContentPackageMongoRepository : MongoRepository<ContentPackageDocument, String> {
    fun save(contentPackageDocument: ContentPackageDocument): ContentPackageDocument
    fun findByName(name: String): ContentPackageDocument?
}
