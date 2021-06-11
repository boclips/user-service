package com.boclips.users.presentation.converters

import com.boclips.users.api.request.AccessRuleRequest
import com.boclips.users.api.response.accessrule.AccessRuleResource
import com.boclips.users.application.exceptions.InvalidVideoTypeException
import com.boclips.users.domain.model.access.AccessRule
import com.boclips.users.domain.model.access.ChannelId
import com.boclips.users.domain.model.access.CollectionId
import com.boclips.users.domain.model.access.DistributionMethod
import com.boclips.users.domain.model.access.PlaybackSource
import com.boclips.users.domain.model.access.VideoId
import com.boclips.users.domain.model.access.VideoType
import org.springframework.stereotype.Service
import java.util.Locale

@Service
class AccessRuleConverter {
    fun toResource(accessRule: AccessRule): AccessRuleResource {
        return when (accessRule) {
            is AccessRule.IncludedCollections -> AccessRuleResource.IncludedCollections(
                name = accessRule.name,
                collectionIds = accessRule.collectionIds.map { it.value }
            )
            is AccessRule.IncludedVideos -> AccessRuleResource.IncludedVideos(
                name = accessRule.name,
                videoIds = accessRule.videoIds.map { it.value }
            )
            is AccessRule.ExcludedVideos -> AccessRuleResource.ExcludedVideos(
                name = accessRule.name,
                videoIds = accessRule.videoIds.map { it.value }
            )
            is AccessRule.ExcludedVideoTypes -> AccessRuleResource.ExcludedVideoTypes(
                name = accessRule.name,
                videoTypes = accessRule.videoTypes.map { it.name }
            )
            is AccessRule.IncludedVideoTypes -> AccessRuleResource.IncludedVideoTypes(
                name = accessRule.name,
                videoTypes = accessRule.videoTypes.map { it.name }
            )
            is AccessRule.ExcludedChannels -> AccessRuleResource.ExcludedChannels(
                name = accessRule.name,
                channelIds = accessRule.channelIds.map { it.value }
            )
            is AccessRule.IncludedChannels -> AccessRuleResource.IncludedChannels(
                name = accessRule.name,
                channelIds = accessRule.channelIds.map { it.value }
            )
            is AccessRule.IncludedDistributionMethods -> AccessRuleResource.IncludedDistributionMethods(
                name = accessRule.name,
                distributionMethods = accessRule.distributionMethods.map { it.name }
            )
            is AccessRule.IncludedVideoVoiceTypes -> AccessRuleResource.IncludedVideoVoiceTypes(
                name = accessRule.name,
                voiceTypes = accessRule.voiceTypes.map { it.name }
            )
            is AccessRule.ExcludedLanguages -> AccessRuleResource.ExcludedLanguages(
                name = accessRule.name,
                languages = accessRule.languages.map { it.toLanguageTag() }.toSet()
            )
            is AccessRule.ExcludedPlaybackSources -> AccessRuleResource.ExcludedPlaybackSources(
                name = accessRule.name,
                sources = accessRule.sources.map { it.name }.toSet()
            )
            is AccessRule.IncludedPrivateChannels ->AccessRuleResource.IncludedPrivateChannels(
                name = accessRule.name,
                channelIds = accessRule.channelIds.map { it.value }
            )
        }
    }

    fun fromRequest(accessRuleRequest: AccessRuleRequest): AccessRule {
        val name = accessRuleRequest.name ?: ""

        return when (accessRuleRequest) {
            is AccessRuleRequest.IncludedCollections -> AccessRule.IncludedCollections(
                name = name,
                collectionIds = accessRuleRequest.collectionIds!!.map { CollectionId(it) }
            )
            is AccessRuleRequest.IncludedVideos -> AccessRule.IncludedVideos(
                name = name,
                videoIds = accessRuleRequest.videoIds!!.map { VideoId(it) }
            )
            is AccessRuleRequest.ExcludedVideoTypes -> AccessRule.ExcludedVideoTypes(
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
                name = name,
                channelIds = accessRuleRequest.channelIds!!.map { ChannelId(it) }
            )
            is AccessRuleRequest.ExcludedVideos -> AccessRule.ExcludedVideos(
                name = name,
                videoIds = accessRuleRequest.videoIds!!.map { VideoId(it) }
            )
            is AccessRuleRequest.ExcludedChannels -> AccessRule.ExcludedChannels(
                name = name,
                channelIds = accessRuleRequest.channelIds!!.map { ChannelId(it) }
            )

            is AccessRuleRequest.IncludedDistributionMethods -> AccessRule.IncludedDistributionMethods(
                name = name,
                distributionMethods = accessRuleRequest.distributionMethods?.mapTo(HashSet()) {
                    DistributionMethod.valueOf(
                        it
                    )
                } ?: emptySet()
            )

            is AccessRuleRequest.ExcludedLanguages -> AccessRule.ExcludedLanguages(
                name = name,
                languages = accessRuleRequest.languages?.map { Locale.forLanguageTag(it) }?.toSet() ?: emptySet()
            )

            is AccessRuleRequest.ExcludedPlaybackSources -> AccessRule.ExcludedPlaybackSources(
                name = name,
                sources = accessRuleRequest.sources?.map { PlaybackSource.valueOf(it) }?.toSet() ?: emptySet()
            )
        }
    }
}
