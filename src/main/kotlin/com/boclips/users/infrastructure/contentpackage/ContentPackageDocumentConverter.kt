package com.boclips.users.infrastructure.contentpackage

import com.boclips.users.domain.model.contentpackage.ContentPackage
import com.boclips.users.domain.model.contentpackage.ContentPackageId
import com.boclips.users.infrastructure.accessrules.AccessRuleDocumentConverter
import org.bson.types.ObjectId

object ContentPackageDocumentConverter {
    fun fromDocument(document: ContentPackageDocument): ContentPackage {
        return ContentPackage(
            id = ContentPackageId(document.id.toHexString()),
            accessRules = document.accessRules.map {
                AccessRuleDocumentConverter().fromDocument(it)
            },
            name = document.name
        )
    }

    fun toDocument(contentPackage: ContentPackage): ContentPackageDocument {
        return ContentPackageDocument(
            id = ObjectId(contentPackage.id.value),
            name = contentPackage.name,
            accessRules = contentPackage.accessRules.map { AccessRuleDocumentConverter().toDocument(it) })
    }
}
