package com.boclips.users.infrastructure.access

import com.boclips.users.domain.model.access.AccessRule
import com.boclips.users.domain.model.access.AccessRuleId
import com.boclips.users.domain.model.access.CollectionId
import com.boclips.users.domain.model.access.ChannelId
import com.boclips.users.domain.model.access.DistributionMethod
import com.boclips.users.domain.model.access.VideoId
import com.boclips.users.domain.model.access.VideoType
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

@Service
class AccessRuleDocumentConverter {
    fun fromDocument(document: AccessRuleDocument): AccessRule {
        return when (document._class) {
            AccessRuleDocument.TYPE_INCLUDED_COLLECTIONS -> AccessRule.IncludedCollections(
                id = AccessRuleId(document._id.toHexString()),
                name = document.name,
                collectionIds = document.collectionIds?.map { CollectionId(it) } ?: blowUp(document)
            )
            AccessRuleDocument.TYPE_INCLUDED_VIDEOS -> AccessRule.IncludedVideos(
                id = AccessRuleId(document._id.toHexString()),
                name = document.name,
                videoIds = document.videoIds?.map { VideoId(it) } ?: blowUp(document)
            )
            AccessRuleDocument.TYPE_EXCLUDED_VIDEOS -> AccessRule.ExcludedVideos(
                id = AccessRuleId(document._id.toHexString()),
                name = document.name,
                videoIds = document.videoIds?.map { VideoId(it) } ?: blowUp(document)
            )
            AccessRuleDocument.TYPE_EXCLUDED_VIDEO_TYPES -> AccessRule.ExcludedVideoTypes(
                id = AccessRuleId(document._id.toHexString()),
                name = document.name,
                videoTypes = document.videoTypes?.map {
                    when (it) {
                        VideoTypeDocument.INSTRUCTIONAL -> VideoType.INSTRUCTIONAL
                        VideoTypeDocument.NEWS -> VideoType.NEWS
                        VideoTypeDocument.STOCK -> VideoType.STOCK
                    }
                } ?: blowUp(document)
            )
            AccessRuleDocument.TYPE_EXCLUDED_CONTENT_PARTNERS -> AccessRule.ExcludedContentPartners(
                id = AccessRuleId(document._id.toHexString()),
                name = document.name,
                channelIds = document.contentPartnerIds?.map { ChannelId(it) } ?: blowUp(document)
            )
            AccessRuleDocument.TYPE_INCLUDED_CHANNELS -> AccessRule.IncludedChannels(
                id = AccessRuleId(document._id.toHexString()),
                name = document.name,
                channelIds = document.channelIds?.map { ChannelId(it) } ?: blowUp(document)
            )
            AccessRuleDocument.TYPE_INCLUDED_DISTRIBUTION_METHODS -> AccessRule.IncludedDistributionMethods(
                id = AccessRuleId(document._id.toHexString()),
                name = document.name,
                distributionMethods = document.distributionMethods?.map {
                    when (it) {
                        AccessRuleDocument.DISTRIBUTION_METHOD_DOWNLOAD -> DistributionMethod.DOWNLOAD
                        AccessRuleDocument.DISTRIBUTION_METHOD_STREAM -> DistributionMethod.STREAM
                        else -> blowUp(document)
                    }
                }?.toSet()  ?: blowUp(document)
            )
            else -> throw IllegalStateException("Unknown type ${document._class} in access rule ${document._id}")
        }
    }

    private fun blowUp(document: AccessRuleDocument): Nothing {
        throw IllegalStateException("Invalid Access Rule document: $document")
    }

    fun toDocument(accessRule: AccessRule): AccessRuleDocument {
        return when (accessRule) {
            is AccessRule.IncludedCollections -> AccessRuleDocument(
                _id = ObjectId(accessRule.id.value),
                _class = AccessRuleDocument.TYPE_INCLUDED_COLLECTIONS,
                name = accessRule.name,
                collectionIds = accessRule.collectionIds.map { it.value }
            )
            is AccessRule.IncludedVideos -> AccessRuleDocument(
                _id = ObjectId(accessRule.id.value),
                _class = AccessRuleDocument.TYPE_INCLUDED_VIDEOS,
                name = accessRule.name,
                videoIds = accessRule.videoIds.map { it.value }
            )
            is AccessRule.ExcludedVideos -> AccessRuleDocument(
                _id = ObjectId(accessRule.id.value),
                _class = AccessRuleDocument.TYPE_EXCLUDED_VIDEOS,
                name = accessRule.name,
                videoIds = accessRule.videoIds.map { it.value }
            )
            is AccessRule.ExcludedVideoTypes -> AccessRuleDocument(
                _id = ObjectId(accessRule.id.value),
                _class = AccessRuleDocument.TYPE_EXCLUDED_VIDEO_TYPES,
                name = accessRule.name,
                videoTypes = accessRule.videoTypes.map {
                    when (it) {
                        VideoType.INSTRUCTIONAL -> VideoTypeDocument.INSTRUCTIONAL
                        VideoType.NEWS -> VideoTypeDocument.NEWS
                        VideoType.STOCK -> VideoTypeDocument.STOCK
                    }
                }
            )
            is AccessRule.ExcludedContentPartners -> AccessRuleDocument(
                _id = ObjectId(accessRule.id.value),
                _class = AccessRuleDocument.TYPE_EXCLUDED_CONTENT_PARTNERS,
                name = accessRule.name,
                contentPartnerIds = accessRule.channelIds.map { it.value }
            )
            is AccessRule.IncludedChannels -> AccessRuleDocument(
                _id = ObjectId(accessRule.id.value),
                _class = AccessRuleDocument.TYPE_INCLUDED_CHANNELS,
                name = accessRule.name,
                channelIds = accessRule.channelIds.map { it.value }
            )
            is AccessRule.IncludedDistributionMethods -> AccessRuleDocument(
                _id = ObjectId(accessRule.id.value),
                _class = AccessRuleDocument.TYPE_INCLUDED_DISTRIBUTION_METHODS,
                name = accessRule.name,
                distributionMethods = accessRule.distributionMethods.map {
                    when (it) {
                        DistributionMethod.DOWNLOAD -> AccessRuleDocument.DISTRIBUTION_METHOD_DOWNLOAD
                        DistributionMethod.STREAM -> AccessRuleDocument.DISTRIBUTION_METHOD_STREAM
                    }
                }
            )
        }
    }
}
