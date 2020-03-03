package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.contentpackage.AccessRule
import com.boclips.users.domain.model.contentpackage.ContentPackage
import com.boclips.users.domain.model.contentpackage.ContentPackageId
import org.bson.types.ObjectId

object ContentPackageFactory {
    fun sampleContentPackage(
        id: String = ObjectId.get().toHexString(),
        name: String = "A Content Package",
        accessRules: List<AccessRule> = emptyList()
    ) = ContentPackage(
        id = ContentPackageId(value = id),
        name = name,
        accessRules = accessRules
    )
}
