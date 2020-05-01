package com.boclips.users.infrastructure.access

import com.boclips.users.domain.model.access.AccessRuleId
import com.boclips.users.domain.model.access.ContentPackage
import com.boclips.users.domain.model.access.ContentPackageId
import org.bson.types.ObjectId

object ContentPackageDocumentConverter {
    fun fromDocument(document: ContentPackageDocument): ContentPackage {
        return ContentPackage(
            id = ContentPackageId(document._id.toHexString()),
            accessRuleIds = document.accessRuleIds.map {
                AccessRuleId(it)
            },
            name = document.name
        )
    }

    fun toDocument(contentPackage: ContentPackage): ContentPackageDocument {
        return ContentPackageDocument(
            _id = ObjectId(contentPackage.id.value),
            name = contentPackage.name,
            accessRuleIds = contentPackage.accessRuleIds.map { it.value })
    }
}
