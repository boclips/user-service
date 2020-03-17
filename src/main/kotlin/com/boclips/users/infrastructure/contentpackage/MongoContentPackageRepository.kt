package com.boclips.users.infrastructure.contentpackage

import com.boclips.users.domain.model.contentpackage.ContentPackage
import com.boclips.users.domain.model.contentpackage.ContentPackageId
import com.boclips.users.domain.service.ContentPackageRepository
import org.springframework.stereotype.Repository

@Repository
class MongoContentPackageRepository(
    private val repository: SpringDataContentPackageMongoRepository
) : ContentPackageRepository {
    override fun save(contentPackage: ContentPackage): ContentPackage {
        val contentPackageDocument = repository.save(
            ContentPackageDocumentConverter.toDocument(
                contentPackage
            )
        )
        return ContentPackageDocumentConverter.fromDocument(contentPackageDocument)
    }

    override fun findById(id: ContentPackageId): ContentPackage? {
        return repository.findById(id.value)
            .orElse(null)
            ?.let {
                ContentPackageDocumentConverter.fromDocument(it)
            }
    }

    override fun findByName(name: String): ContentPackage? {
        return repository.findByName(name)
            ?.let {
                ContentPackageDocumentConverter.fromDocument(it)
            }
    }
}
