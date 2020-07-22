package com.boclips.users.presentation.converters

import com.boclips.users.api.request.AccessRuleRequest
import com.boclips.users.api.response.accessrule.AccessRuleResource
import com.boclips.users.application.exceptions.InvalidVideoTypeException
import com.boclips.users.domain.model.access.AccessRule
import com.boclips.users.domain.model.access.AccessRuleId
import com.boclips.users.domain.model.access.ChannelId
import com.boclips.users.domain.model.access.CollectionId
import com.boclips.users.domain.model.access.DistributionMethod
import com.boclips.users.domain.model.access.VideoId
import com.boclips.users.domain.model.access.VideoType
import com.boclips.users.domain.service.UniqueId
import org.springframework.stereotype.Service

@Service
class AccessRuleConverter(
) {
    fun toResource(accessRule: AccessRule): AccessRuleResource {
        return when (accessRule) {
            is AccessRule.IncludedCollections -> AccessRuleResource.IncludedCollections(
                id = accessRule.id.value,
                name = accessRule.name,
                collectionIds = accessRule.collectionIds.map { it.value }
            )
            is AccessRule.IncludedVideos -> AccessRuleResource.IncludedVideos(
                id = accessRule.id.value,
                name = accessRule.name,
                videoIds = accessRule.videoIds.map { it.value }
            )
            is AccessRule.ExcludedVideos -> AccessRuleResource.ExcludedVideos(
                id = accessRule.id.value,
                name = accessRule.name,
                videoIds = accessRule.videoIds.map { it.value }
            )
            is AccessRule.ExcludedVideoTypes -> AccessRuleResource.ExcludedVideoTypes(
                id = accessRule.id.value,
                name = accessRule.name,
                videoTypes = accessRule.videoTypes.map { it.name }
            )
            is AccessRule.ExcludedChannels -> AccessRuleResource.ExcludedChannels(
                id = accessRule.id.value,
                name = accessRule.name,
                channelIds = accessRule.channelIds.map { it.value }
            )
            is AccessRule.IncludedChannels -> AccessRuleResource.IncludedChannels(
                id = accessRule.id.value,
                name = accessRule.name,
                channelIds = accessRule.channelIds.map { it.value }
            )
            is AccessRule.IncludedDistributionMethods -> AccessRuleResource.IncludedDistributionMethod(
                id = accessRule.id.value,
                name = accessRule.name,
                distributionMethods = accessRule.distributionMethods.map { it.name }
            )
        }
    }

    fun fromRequest(accessRuleRequest: AccessRuleRequest): AccessRule {
        val name = accessRuleRequest.name ?: ""
        val id = AccessRuleId(UniqueId())

        return when (accessRuleRequest) {
            is AccessRuleRequest.IncludedCollections -> AccessRule.IncludedCollections(
                id = id,
                name = name,
                collectionIds = accessRuleRequest.collectionIds!!.map { CollectionId(it) }
            )
            is AccessRuleRequest.IncludedVideos -> AccessRule.IncludedVideos(
                id = id,
                name = name,
                videoIds = accessRuleRequest.videoIds!!.map { VideoId(it) }
            )
            is AccessRuleRequest.ExcludedVideoTypes -> AccessRule.ExcludedVideoTypes(
                    id = id,
                    name = name,
                    videoTypes = accessRuleRequest.videoTypes!!.map {
                        when (it.toUpperCase()) {
                            "NEWS" -> VideoType.NEWS
                            "INSTRUCTIONAL" -> VideoType.INSTRUCTIONAL
                            "STOCK" -> VideoType.STOCK
                            else -> throw InvalidVideoTypeException(it)
                        }
                    }
            )

            is AccessRuleRequest.IncludedChannels -> AccessRule.IncludedChannels(
                id = id,
                name = name,
                channelIds = accessRuleRequest.channelIds!!.map { ChannelId(it) }
            )
            is AccessRuleRequest.ExcludedVideos -> AccessRule.ExcludedVideos(
                id = id,
                name = name,
                videoIds = accessRuleRequest.videoIds!!.map { VideoId(it) }
            )
            is AccessRuleRequest.ExcludedChannels  -> AccessRule.ExcludedChannels(
                id = id,
                name = name,
                channelIds = accessRuleRequest.channelIds!!.map { ChannelId(it) }
            )

            is AccessRuleRequest.IncludedDistributionMethod -> AccessRule.IncludedDistributionMethods(
                id = id,
                name = name,
                distributionMethods = accessRuleRequest.distributionMethods?.mapTo(HashSet()) { DistributionMethod.valueOf(it) } ?: emptySet()
            )
        }
    }
}
