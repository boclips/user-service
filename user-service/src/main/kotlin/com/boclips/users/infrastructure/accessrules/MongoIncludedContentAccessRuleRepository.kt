package com.boclips.users.infrastructure.accessrules

import com.boclips.users.domain.model.contentpackage.AccessRule
import com.boclips.users.domain.model.contentpackage.CollectionId
import com.boclips.users.domain.model.contentpackage.VideoId
import com.boclips.users.domain.service.IncludedContentAccessRuleRepository
import org.bson.types.ObjectId
import org.springframework.stereotype.Repository

@Repository
class MongoIncludedContentAccessRuleRepository(
    private val includedCollectionsAccessRuleDocumentMongoRepository: IncludedCollectionsAccessRuleDocumentMongoRepository,
    private val includedVideosAccessRuleDocumentMongoRepository: IncludedVideosAccessRuleDocumentMongoRepository,
    private val accessRuleDocumentConverter: AccessRuleDocumentConverter
) : IncludedContentAccessRuleRepository {
    override fun saveIncludedCollectionsAccessRule(
        name: String,
        collectionIds: List<CollectionId>
    ): AccessRule.IncludedCollections {
        return accessRuleDocumentConverter.fromDocument(
            includedCollectionsAccessRuleDocumentMongoRepository.save(
                AccessRuleDocument.IncludedCollections().apply {
                    this.id = ObjectId()
                    this.name = name
                    this.collectionIds = collectionIds.map { it.value }
                }
            )
        ) as AccessRule.IncludedCollections
    }

    override fun saveIncludedVideosAccessRule(name: String, videoIds: List<VideoId>): AccessRule.IncludedVideos {
        return accessRuleDocumentConverter.fromDocument(
            includedVideosAccessRuleDocumentMongoRepository.save(
                AccessRuleDocument.IncludedVideos().apply {
                    this.id = ObjectId()
                    this.name = name
                    this.videoIds = videoIds.map { it.value }
                }
            )
        ) as AccessRule.IncludedVideos
    }
}
