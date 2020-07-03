package com.boclips.users.presentation.converters

import com.boclips.users.api.response.accessrule.AccessRuleResource
import com.boclips.users.domain.model.access.AccessRule
import com.boclips.users.presentation.hateoas.AccessRuleLinkBuilder
import com.boclips.users.presentation.hateoas.IncludedAccessRuleLinkBuilder
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
            is AccessRule.ExcludedChannels -> AccessRuleResource.ExcludedChannels(
                name = accessRule.name,
                channelIds = accessRule.channelIds.map { it.value },
                _links = listOfNotNull(accessRuleLinkBuilder.self(accessRule.id)).map { it.rel.value() to it }.toMap()
            )
            is AccessRule.IncludedChannels -> AccessRuleResource.IncludedChannels(
                name = accessRule.name,
                channelIds = accessRule.channelIds.map { it.value },
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
