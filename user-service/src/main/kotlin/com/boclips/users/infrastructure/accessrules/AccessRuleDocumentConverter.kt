package com.boclips.users.infrastructure.accessrules

import com.boclips.users.domain.model.contentpackage.AccessRule
import com.boclips.users.domain.model.contentpackage.AccessRuleId
import com.boclips.users.domain.model.contentpackage.CollectionId
import com.boclips.users.domain.model.contentpackage.ContentPartnerId
import com.boclips.users.domain.model.contentpackage.DistributionMethod
import com.boclips.users.domain.model.contentpackage.DistributionMethodDocument
import com.boclips.users.domain.model.contentpackage.VideoId
import com.boclips.users.domain.model.contentpackage.VideoType
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

@Service
class AccessRuleDocumentConverter {
    fun fromDocument(document: AccessRuleDocument): AccessRule {
        return when (document) {
            is AccessRuleDocument.IncludedCollections -> AccessRule.IncludedCollections(
                id = AccessRuleId(document.id.toHexString()),
                name = document.name,
                collectionIds = document.collectionIds.map { CollectionId(it) }
            )
            is AccessRuleDocument.IncludedVideos -> AccessRule.IncludedVideos(
                id = AccessRuleId(document.id.toHexString()),
                name = document.name,
                videoIds = document.videoIds.map { VideoId(it) }
            )
            is AccessRuleDocument.ExcludedVideos -> AccessRule.ExcludedVideos(
                id = AccessRuleId(document.id.toHexString()),
                name = document.name,
                videoIds = document.videoIds.map { VideoId(it) }
            )
            is AccessRuleDocument.ExcludedVideoTypes -> AccessRule.ExcludedVideoTypes(
                id = AccessRuleId(document.id.toHexString()),
                name = document.name,
                videoTypes = document.videoTypes.map {
                    when (it) {
                        VideoTypeDocument.INSTRUCTIONAL -> VideoType.INSTRUCTIONAL
                        VideoTypeDocument.NEWS -> VideoType.NEWS
                        VideoTypeDocument.STOCK -> VideoType.STOCK
                    }
                }
            )
            is AccessRuleDocument.ExcludedContentPartners -> AccessRule.ExcludedContentPartners(
                id = AccessRuleId(document.id.toHexString()),
                name = document.name,
                contentPartnerIds = document.contentPartnerIds.map { ContentPartnerId(it) }
            )
            is AccessRuleDocument.IncludedDistributionMethods -> AccessRule.IncludedDistributionMethods(
                id = AccessRuleId(document.id.toHexString()),
                name = document.name,
                distributionMethods = document.distributionMethods.map {
                    when (it) {
                        DistributionMethodDocument.DOWNLOAD -> DistributionMethod.DOWNLOAD
                        DistributionMethodDocument.STREAM -> DistributionMethod.STREAM
                    }
                }.toSet()
            )
        }
    }

    fun toDocument(accessRule: AccessRule): AccessRuleDocument {
        return when (accessRule) {
            is AccessRule.IncludedCollections -> AccessRuleDocument.IncludedCollections().apply {
                id = ObjectId(accessRule.id.value)
                name = accessRule.name
                collectionIds = accessRule.collectionIds.map { it.value }
            }
            is AccessRule.IncludedVideos -> AccessRuleDocument.IncludedVideos().apply {
                id = ObjectId(accessRule.id.value)
                name = accessRule.name
                videoIds = accessRule.videoIds.map { it.value }
            }
            is AccessRule.ExcludedVideos -> AccessRuleDocument.ExcludedVideos().apply {
                id = ObjectId(accessRule.id.value)
                name = accessRule.name
                videoIds = accessRule.videoIds.map { it.value }
            }
            is AccessRule.ExcludedVideoTypes -> AccessRuleDocument.ExcludedVideoTypes().apply {
                id = ObjectId(accessRule.id.value)
                name = accessRule.name
                videoTypes = accessRule.videoTypes.map {
                    when (it) {
                        VideoType.INSTRUCTIONAL -> VideoTypeDocument.INSTRUCTIONAL
                        VideoType.NEWS -> VideoTypeDocument.NEWS
                        VideoType.STOCK -> VideoTypeDocument.STOCK
                    }
                }
            }
            is AccessRule.ExcludedContentPartners -> AccessRuleDocument.ExcludedContentPartners().apply {
                id = ObjectId(accessRule.id.value)
                name = accessRule.name
                contentPartnerIds = accessRule.contentPartnerIds.map { it.value }
            }
            is AccessRule.IncludedDistributionMethods -> AccessRuleDocument.IncludedDistributionMethods().apply {
                id = ObjectId(accessRule.id.value)
                name = accessRule.name
                distributionMethods = accessRule.distributionMethods.map {
                    when (it) {
                        DistributionMethod.DOWNLOAD -> DistributionMethodDocument.DOWNLOAD
                        DistributionMethod.STREAM -> DistributionMethodDocument.STREAM
                    }
                }
            }
        }
    }
}
