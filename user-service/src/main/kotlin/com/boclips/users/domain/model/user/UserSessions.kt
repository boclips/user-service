package com.boclips.users.domain.model.user

import java.time.Instant

class UserSessions(val lastAccess: Instant?) {
    fun hasLoggedIn(): Boolean {
        return lastAccess != null
    }
}

