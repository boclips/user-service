package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.UserSessions
import java.time.Instant

class UserSessionsFactory {
    companion object {
        fun sample(lastAccess: Instant? = Instant.now()): UserSessions {
            return UserSessions(lastAccess = lastAccess)
        }
    }
}
