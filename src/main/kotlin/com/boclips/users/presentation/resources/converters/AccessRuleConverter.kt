package com.boclips.users.presentation.resources.converters

import com.boclips.users.domain.model.contentpackage.AccessRule
import com.boclips.users.presentation.hateoas.AccessRuleLinkBuilder
import com.boclips.users.presentation.hateoas.SelectedAccessRuleLinkBuilder
import com.boclips.users.presentation.resources.AccessRuleResource
import org.springframework.stereotype.Service

@Service
class AccessRuleConverter(
    private val accessRuleLinkBuilder: AccessRuleLinkBuilder,
    private val selectedAccessRuleLinkBuilder: SelectedAccessRuleLinkBuilder
) {
    fun toResource(accessRule: AccessRule): AccessRuleResource {
        return when (accessRule) {
            is AccessRule.SelectedCollections -> AccessRuleResource.SelectedCollections(
                name = accessRule.name,
                collectionIds = accessRule.collectionIds.map { it.value },
                _links = listOfNotNull(
                    selectedAccessRuleLinkBuilder.addCollection(accessRuleId = accessRule.id.value),
                    selectedAccessRuleLinkBuilder.removeCollection(accessRuleId = accessRule.id.value),
                    accessRuleLinkBuilder.self(accessRule.id)
                ).map { it.rel.value() to it }.toMap()
            )
            is AccessRule.SelectedVideos -> AccessRuleResource.SelectedVideos(
                name = accessRule.name,
                videoIds = accessRule.videoIds.map { it.value },
                _links = listOfNotNull(accessRuleLinkBuilder.self(accessRule.id)).map { it.rel.value() to it }.toMap()
            )
        }
    }
}
