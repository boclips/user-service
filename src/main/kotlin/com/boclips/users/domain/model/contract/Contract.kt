package com.boclips.users.domain.model.contract

sealed class Contract {
    data class SelectedContent(
        override val id: ContractId,
        override val name: String,
        val collectionIds: List<CollectionId>
    ) : Contract()

    abstract val id: ContractId
    abstract val name: String
}