package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.TeacherPlatformAttributes

class TeacherPlatformAttributesFactory {
    companion object {
        fun sample(
            shareCode: String? = "DFGY",
            hasLifetimeAccess: Boolean = false
        ): TeacherPlatformAttributes {
            return TeacherPlatformAttributes(
                shareCode = shareCode,
                hasLifetimeAccess = hasLifetimeAccess
            )
        }
    }
}
