package com.boclips.users.domain.model.contentpackage

sealed class AccessRule {
    data class IncludedCollections(
        override val id: AccessRuleId,
        override val name: String,
        val collectionIds: List<CollectionId>
    ) : AccessRule()

    data class IncludedVideos(
        override val id: AccessRuleId,
        override val name: String,
        val videoIds: List<VideoId>
    ) : AccessRule()

    abstract val id: AccessRuleId
    abstract val name: String
}
