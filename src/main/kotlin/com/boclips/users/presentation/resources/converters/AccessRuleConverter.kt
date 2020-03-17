package com.boclips.users.presentation.resources.converters

import com.boclips.users.domain.model.contentpackage.AccessRule
import com.boclips.users.presentation.hateoas.AccessRuleLinkBuilder
import com.boclips.users.presentation.hateoas.IncludedAccessRuleLinkBuilder
import com.boclips.users.presentation.resources.AccessRuleResource
import org.springframework.stereotype.Service

@Service
class AccessRuleConverter(
    private val accessRuleLinkBuilder: AccessRuleLinkBuilder,
    private val includedAccessRuleLinkBuilder: IncludedAccessRuleLinkBuilder
) {
    fun toResource(accessRule: AccessRule): AccessRuleResource {
        return when (accessRule) {
            is AccessRule.IncludedCollections -> AccessRuleResource.IncludedCollections(
                name = accessRule.name,
                collectionIds = accessRule.collectionIds.map { it.value },
                _links = listOfNotNull(
                    includedAccessRuleLinkBuilder.addCollection(accessRuleId = accessRule.id.value),
                    includedAccessRuleLinkBuilder.removeCollection(accessRuleId = accessRule.id.value),
                    accessRuleLinkBuilder.self(accessRule.id)
                ).map { it.rel.value() to it }.toMap()
            )
            is AccessRule.IncludedVideos -> AccessRuleResource.IncludedVideos(
                name = accessRule.name,
                videoIds = accessRule.videoIds.map { it.value },
                _links = listOfNotNull(accessRuleLinkBuilder.self(accessRule.id)).map { it.rel.value() to it }.toMap()
            )
            is AccessRule.ExcludedVideos -> AccessRuleResource.ExcludedVideos(
                name = accessRule.name,
                videoIds = accessRule.videoIds.map { it.value },
                _links = listOfNotNull(accessRuleLinkBuilder.self(accessRule.id)).map { it.rel.value() to it }.toMap()
            )
            is AccessRule.ExcludedVideoTypes -> AccessRuleResource.ExcludedVideoTypes(
                name = accessRule.name,
                videoTypes = accessRule.videoTypes.map { it.name },
                _links = listOfNotNull(accessRuleLinkBuilder.self(accessRule.id)).map { it.rel.value() to it }.toMap()
            )
            is AccessRule.ExcludedContentPartners -> AccessRuleResource.ExcludedContentPartners(
                name = accessRule.name,
                contentPartnerIds = accessRule.contentPartnerIds.map { it.value },
                _links = listOfNotNull(accessRuleLinkBuilder.self(accessRule.id)).map { it.rel.value() to it }.toMap()
            )
            is AccessRule.IncludedDistributionMethods -> AccessRuleResource.IncludedDistributionMethod(
                name = accessRule.name,
                distributionMethods = accessRule.distributionMethods.map { it.name },
                _links = listOfNotNull(accessRuleLinkBuilder.self(accessRule.id)).map { it.rel.value() to it }.toMap()
            )
        }
    }
}
