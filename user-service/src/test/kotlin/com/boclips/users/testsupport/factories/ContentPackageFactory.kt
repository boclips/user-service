package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.access.AccessRule
import com.boclips.users.domain.model.access.ContentPackage
import com.boclips.users.domain.model.access.ContentPackageId
import org.bson.types.ObjectId

object ContentPackageFactory {
    fun sample(
        id: String = ObjectId.get().toHexString(),
        name: String = "A Content Package",
        accessRules: List<AccessRule> = emptyList()
    ) = ContentPackage(
        id = ContentPackageId(value = id),
        name = name,
        accessRules = accessRules
    )
}
