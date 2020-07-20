package com.boclips.users.presentation.converters

import com.boclips.users.api.response.accessrule.ContentPackageResource
import com.boclips.users.api.response.accessrule.ContentPackagesResource
import com.boclips.users.api.response.accessrule.ContentPackagesWrapperResource
import com.boclips.users.domain.model.access.ContentPackage
import com.boclips.users.presentation.hateoas.ContentPackageLinkBuilder
import org.springframework.stereotype.Service

@Service
class ContentPackageConverter(
    val accessRuleConverter: AccessRuleConverter,
    val contentPackageLinkBuilder: ContentPackageLinkBuilder
) {
    fun toContentPackagesResource(contentPackages: List<ContentPackage>): ContentPackagesResource =
        ContentPackagesResource(
            _embedded = ContentPackagesWrapperResource(
                contentPackages = contentPackages.map { toContentPackageResource(it) }
            ),
            _links = null
        )

    fun toContentPackageResource(contentPackage: ContentPackage): ContentPackageResource =
        ContentPackageResource(
            id = contentPackage.id.value,
            name = contentPackage.name,
            accessRules = contentPackage.accessRules.map { accessRuleConverter.toResource(it) },
            _links = listOfNotNull(
                contentPackageLinkBuilder.self(contentPackageId = contentPackage.id).let { link ->
                    link.rel.value() to link
                }).toMap()
        )
}
