package com.boclips.users.testsupport.factories

import com.boclips.users.api.request.AccessRuleRequest

class AccessRuleRequestFactory {
    companion object {
        fun sampleIncludedCollectionsAccessRuleRequest(
            name: String = "included collections access rule",
            collectionIds: List<String> = emptyList()
        ): AccessRuleRequest {
            val request = AccessRuleRequest.IncludedCollections()
            request.collectionIds = collectionIds
            request.name = name
            return request
        }

        fun sampleIncludedVideosAccessRuleRequest(
            name: String = "included videos access rule",
            videoIds: List<String> = emptyList()
        ): AccessRuleRequest {
            val request = AccessRuleRequest.IncludedVideos()
            request.videoIds = videoIds
            request.name = name
            return request
        }

        fun sampleIncludedChannelsAccessRuleRequest(
            name: String = "included channels access rule",
            channelIds: List<String> = emptyList()
        ): AccessRuleRequest {
            val request = AccessRuleRequest.IncludedChannels()
            request.channelIds = channelIds
            request.name = name
            return request
        }

        fun sampleIncludedDistributionMethodsAccessRuleRequest(
            name: String? = "included distribution method access rule",
            distributionMethods: List<String> = emptyList()
        ): AccessRuleRequest {
            val request = AccessRuleRequest.IncludedDistributionMethods()
            request.distributionMethods = distributionMethods
            request.name = name
            return request
        }

        fun sampleExcludedVideosAccessRuleRequest(
            name: String = "excluded videos access rule",
            videoIds: List<String> = emptyList()
        ): AccessRuleRequest {
            val request = AccessRuleRequest.ExcludedVideos()
            request.videoIds = videoIds
            request.name = name
            return request
        }

        fun sampleExcludedChannelsAccessRuleRequest(
            name: String = "excluded channels access rule",
            channelIds: List<String> = emptyList()
        ): AccessRuleRequest {
            val request = AccessRuleRequest.ExcludedChannels()
            request.channelIds = channelIds
            request.name = name
            return request
        }

        fun sampleExcludedVideoTypesAccessRuleRequest(
            name: String = "excluded video types access rule",
            videoTypes: List<String> = emptyList()
        ): AccessRuleRequest {
            val request = AccessRuleRequest.ExcludedVideoTypes()
            request.videoTypes = videoTypes
            request.name = name
            return request
        }
    }
}
