package com.boclips.users.presentation.requests

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import javax.validation.constraints.NotEmpty

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = CreateContractRequest.SelectedContent::class, name = "SelectedContent")
)
sealed class CreateContractRequest {
    class SelectedContent : CreateContractRequest() {
        @field:NotEmpty
        var collectionIds: List<String>? = null
    }

    @field:NotEmpty
    var name: String? = null
}
