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

    data class IncludedDistributionMethods(
        override val id: AccessRuleId,
        override val name: String,
        val distributionMethods: Set<DistributionMethod>
    ) : AccessRule()

    data class ExcludedVideos(
        override val id: AccessRuleId,
        override val name: String,
        val videoIds: List<VideoId>
    ) : AccessRule()

    data class ExcludedVideoTypes(
        override val id: AccessRuleId,
        override val name: String,
        val videoTypes: List<VideoType>
    ) : AccessRule()

    data class ExcludedContentPartners(
        override val id: AccessRuleId,
        override val name: String,
        val contentPartnerIds: List<ContentPartnerId>
    ) : AccessRule()

    abstract val id: AccessRuleId
    abstract val name: String
}
