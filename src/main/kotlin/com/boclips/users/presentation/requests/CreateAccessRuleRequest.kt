package com.boclips.users.presentation.requests

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import javax.validation.constraints.NotEmpty

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = CreateAccessRuleRequest.SelectedCollections::class, name = "SelectedCollections"),
    JsonSubTypes.Type(value = CreateAccessRuleRequest.SelectedVideos::class, name = "SelectedVideos")
)
sealed class CreateAccessRuleRequest {
    class SelectedCollections : CreateAccessRuleRequest() {
        @field:NotEmpty
        var collectionIds: List<String>? = null
    }

    class SelectedVideos : CreateAccessRuleRequest() {
        @field:NotEmpty
        var videoIds: List<String>? = null
    }

    @field:NotEmpty
    var name: String? = null
}
