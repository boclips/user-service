package com.boclips.users.infrastructure.accessrules

import com.boclips.users.domain.model.contentpackage.AccessRule
import com.boclips.users.domain.model.contentpackage.CollectionId
import com.boclips.users.domain.model.contentpackage.VideoId
import com.boclips.users.domain.service.SelectedContentAccessRuleRepository
import org.bson.types.ObjectId
import org.springframework.stereotype.Repository

@Repository
class MongoSelectedContentAccessRuleRepository(
    private val selectedCollectionsAccessRuleDocumentMongoRepository: SelectedCollectionsAccessRuleDocumentMongoRepository,
    private val selectedVideosAccessRuleDocumentMongoRepository: SelectedVideosAccessRuleDocumentMongoRepository,
    private val accessRuleDocumentConverter: AccessRuleDocumentConverter
) : SelectedContentAccessRuleRepository {
    override fun saveSelectedCollectionsAccessRule(
        name: String,
        collectionIds: List<CollectionId>
    ): AccessRule.SelectedCollections {
        return accessRuleDocumentConverter.fromDocument(
            selectedCollectionsAccessRuleDocumentMongoRepository.save(
                AccessRuleDocument.SelectedCollections().apply {
                    this.id = ObjectId()
                    this.name = name
                    this.collectionIds = collectionIds.map { it.value }
                }
            )
        ) as AccessRule.SelectedCollections
    }

    override fun saveSelectedVideosAccessRule(name: String, videoIds: List<VideoId>): AccessRule.SelectedVideos {
        return accessRuleDocumentConverter.fromDocument(
            selectedVideosAccessRuleDocumentMongoRepository.save(
                AccessRuleDocument.SelectedVideos().apply {
                    this.id = ObjectId()
                    this.name = name
                    this.videoIds = videoIds.map { it.value }
                }
            )
        ) as AccessRule.SelectedVideos
    }
}
