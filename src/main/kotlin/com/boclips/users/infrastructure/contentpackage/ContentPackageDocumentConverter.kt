package com.boclips.users.infrastructure.contentpackage

import com.boclips.users.domain.model.contentpackage.AccessRuleId
import com.boclips.users.domain.model.contentpackage.ContentPackage
import com.boclips.users.domain.model.contentpackage.ContentPackageId
import org.bson.types.ObjectId

object ContentPackageDocumentConverter {
    fun fromDocument(document: ContentPackageDocument): ContentPackage {
        return ContentPackage(
            id = ContentPackageId(document.id.toHexString()),
            accessRuleIds = document.accessRuleIds.map {
                AccessRuleId(it)
            },
            name = document.name
        )
    }

    fun toDocument(contentPackage: ContentPackage): ContentPackageDocument {
        return ContentPackageDocument(
            id = ObjectId(contentPackage.id.value),
            name = contentPackage.name,
            accessRuleIds = contentPackage.accessRuleIds.map { it.value })
    }
}
