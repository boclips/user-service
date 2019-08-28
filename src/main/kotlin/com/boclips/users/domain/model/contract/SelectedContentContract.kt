package com.boclips.users.domain.model.contract

data class SelectedContentContract(
    val id: ContractId,
    val name: String,
    val collectionIds: List<CollectionId>
)