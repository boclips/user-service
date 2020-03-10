package com.boclips.users.presentation.requests

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import javax.validation.constraints.NotEmpty

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = CreateAccessRuleRequest.IncludedCollections::class, name = "IncludedCollections"),
    JsonSubTypes.Type(value = CreateAccessRuleRequest.IncludedVideos::class, name = "IncludedVideos")
)
sealed class CreateAccessRuleRequest {
    class IncludedCollections : CreateAccessRuleRequest() {
        @field:NotEmpty
        var collectionIds: List<String>? = null
    }

    class IncludedVideos : CreateAccessRuleRequest() {
        @field:NotEmpty
        var videoIds: List<String>? = null
    }

    @field:NotEmpty
    var name: String? = null
}
