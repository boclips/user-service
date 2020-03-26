package com.boclips.users.presentation.converters

import com.boclips.users.application.commands.GetAccessRuleById
import com.boclips.users.domain.model.contentpackage.ContentPackage
import com.boclips.users.presentation.hateoas.ContentPackageLinkBuilder
import com.boclips.users.api.response.accessrule.ContentPackageResource
import org.springframework.stereotype.Service

@Service
class ContentPackageConverter(
    val getAccessRuleById: GetAccessRuleById,
    val accessRuleConverter: AccessRuleConverter,
    val contentPackageLinkBuilder: ContentPackageLinkBuilder
) {
    fun toResource(contentPackage: ContentPackage): ContentPackageResource =
        ContentPackageResource(
            id = contentPackage.id.value,
            name = contentPackage.name,
            accessRules = contentPackage.accessRuleIds.map { accessRuleConverter.toResource(getAccessRuleById(it.value)) },
            _links = listOfNotNull(
                contentPackageLinkBuilder.self(contentPackageId = contentPackage.id).let { link ->
                    link.rel.value() to link
                }).toMap()
        )
}
