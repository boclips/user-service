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
        return ContentPackageDocumentConverter.fromDocument(
            repository.save(
                ContentPackageDocumentConverter.toDocument(
                    contentPackage
                )
            )
        )
    }

    override fun findById(id: ContentPackageId): ContentPackage? {
        return repository.findById(id.value)
            .orElse(null)
            ?.let {
                ContentPackageDocumentConverter.fromDocument(it)
            }
    }
}
