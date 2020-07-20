package com.boclips.users.api.request

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import javax.validation.constraints.NotEmpty

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = AccessRuleRequest.IncludedCollections::class, name = "IncludedCollections"),
    JsonSubTypes.Type(value = AccessRuleRequest.IncludedVideos::class, name = "IncludedVideos"),
    JsonSubTypes.Type(value = AccessRuleRequest.ExcludedVideoTypes::class, name = "ExcludedVideoTypes"),
    JsonSubTypes.Type(value = AccessRuleRequest.IncludedChannels::class, name = "IncludedChannels"),
    JsonSubTypes.Type(value = AccessRuleRequest.ExcludedVideos::class, name = "ExcludedVideos"),
    JsonSubTypes.Type(value = AccessRuleRequest.IncludedDistributionMethod::class, name = "IncludedDistributionMethod"),
    JsonSubTypes.Type(value = AccessRuleRequest.ExcludedChannels::class, name = "ExcludedChannels")
)
sealed class AccessRuleRequest {
    class IncludedCollections : AccessRuleRequest() {
        @field:NotEmpty
        var collectionIds: List<String>? = null
    }

    class IncludedVideos : AccessRuleRequest() {
        @field:NotEmpty
        var videoIds: List<String>? = null
    }

    class ExcludedVideos : AccessRuleRequest() {
        @field:NotEmpty
        var videoIds: List<String>? = null
    }

    class ExcludedVideoTypes : AccessRuleRequest() {
        @field: NotEmpty
        var videoTypes: List<String>? = null
    }

    class IncludedChannels: AccessRuleRequest() {
        @field:NotEmpty
        var channelIds: List<String>? = null
    }

    class ExcludedChannels: AccessRuleRequest() {
        @field:NotEmpty
        var channelIds: List<String>? = null
    }

    class IncludedDistributionMethod: AccessRuleRequest() {
        @field:NotEmpty
        var distributionMethods: List<String>? = null
    }

    @field:NotEmpty
    var name: String? = null
}
