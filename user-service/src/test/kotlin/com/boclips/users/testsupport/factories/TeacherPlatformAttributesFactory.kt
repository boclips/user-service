package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.user.TeacherPlatformAttributes

class TeacherPlatformAttributesFactory {
    companion object {
        fun sample(
            hasLifetimeAccess: Boolean = false
        ): TeacherPlatformAttributes {
            return TeacherPlatformAttributes(
                hasLifetimeAccess = hasLifetimeAccess
            )
        }
    }
}
