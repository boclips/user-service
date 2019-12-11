package com.boclips.users.domain.model.contract

sealed class Contract {
    data class SelectedCollections(
        override val id: ContractId,
        override val name: String,
        val collectionIds: List<CollectionId>
    ) : Contract()

    data class SelectedVideos(
        override val id: ContractId,
        override val name: String,
        val videoIds: List<VideoId>
    ) : Contract()

    abstract val id: ContractId
    abstract val name: String
}