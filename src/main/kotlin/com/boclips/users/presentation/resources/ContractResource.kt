package com.boclips.users.presentation.resources

import org.springframework.hateoas.core.Relation

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
