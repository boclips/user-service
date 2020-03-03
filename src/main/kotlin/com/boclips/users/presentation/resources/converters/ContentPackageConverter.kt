package com.boclips.users.presentation.resources.converters

import com.boclips.users.domain.model.contentpackage.ContentPackage
import com.boclips.users.presentation.hateoas.ContentPackageLinkBuilder
import com.boclips.users.presentation.resources.ContentPackageResource
import org.springframework.stereotype.Service

@Service
class ContentPackageConverter(
    val accessRuleConverter: AccessRuleConverter,
    val contentPackageLinkBuilder: ContentPackageLinkBuilder
) {
    fun toResource(contentPackage: ContentPackage): ContentPackageResource = ContentPackageResource(
        id = contentPackage.id.value,
        name = contentPackage.name,
        accessRules = contentPackage.accessRules.map { accessRuleConverter.toResource(it) },
        _links = listOfNotNull(
            contentPackageLinkBuilder.self(contentPackageId = contentPackage.id).let { link ->
                link.rel.value() to link
            }).toMap()
    )
}
