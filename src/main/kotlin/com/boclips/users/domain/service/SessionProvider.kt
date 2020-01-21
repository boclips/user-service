package com.boclips.users.domain.service

import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.UserSessions

interface SessionProvider {
    fun getUserSessions(id: UserId): UserSessions
}
