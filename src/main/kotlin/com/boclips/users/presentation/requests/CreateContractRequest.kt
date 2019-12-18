package com.boclips.users.presentation.requests

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import javax.validation.constraints.NotEmpty

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = CreateContractRequest.SelectedCollections::class, name = "SelectedCollections"),
    JsonSubTypes.Type(value = CreateContractRequest.SelectedVideos::class, name = "SelectedVideos")
)
sealed class CreateContractRequest {
    class SelectedCollections : CreateContractRequest() {
        @field:NotEmpty
        var collectionIds: List<String>? = null
    }

    class SelectedVideos : CreateContractRequest() {
        @field:NotEmpty
        var videoIds: List<String>? = null
    }

    @field:NotEmpty
    var name: String? = null
}
