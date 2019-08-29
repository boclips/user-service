package com.boclips.users.presentation.resources

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.springframework.hateoas.core.Relation

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = ContractResource.SelectedContent::class, name = "SelectedContent")
)
sealed class ContractResource {
    @Relation(collectionRelation = "contracts")
    data class SelectedContent(
        override val id: String,
        override val name: String,
        val collectionIds: List<String>
    ) : ContractResource()

    abstract val id: String
    abstract val name: String
}
