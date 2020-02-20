package com.boclips.users.domain.model.accessrules

sealed class AccessRule {
    data class SelectedCollections(
        override val id: AccessRuleId,
        override val name: String,
        val collectionIds: List<CollectionId>
    ) : AccessRule()

    data class SelectedVideos(
        override val id: AccessRuleId,
        override val name: String,
        val videoIds: List<VideoId>
    ) : AccessRule()

    abstract val id: AccessRuleId
    abstract val name: String
}


/*
Started on accessRule rename, need to update the endpoint and figure out contract document changes

 */
//////////////////
