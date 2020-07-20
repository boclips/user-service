package com.boclips.users.infrastructure.access

import com.boclips.users.domain.model.access.ContentPackage
import com.boclips.users.domain.model.access.ContentPackageId
import org.bson.types.ObjectId

object ContentPackageDocumentConverter {
    fun fromDocument(document: ContentPackageDocument): ContentPackage {
        return ContentPackage(
            id = ContentPackageId(document._id.toHexString()),
            name = document.name,
            accessRules = document.accessRules.map { AccessRuleDocumentConverter.fromDocument(it) }
        )
    }

    fun toDocument(contentPackage: ContentPackage): ContentPackageDocument {
        return ContentPackageDocument(
            _id = ObjectId(contentPackage.id.value),
            name = contentPackage.name,
            accessRules = contentPackage.accessRules.map { AccessRuleDocumentConverter.toDocument(it) }
        )
    }
}
