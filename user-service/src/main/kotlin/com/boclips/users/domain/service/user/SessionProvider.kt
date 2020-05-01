package com.boclips.users.domain.service.user

import com.boclips.users.domain.model.user.UserId
import com.boclips.users.domain.model.user.UserSessions

interface SessionProvider {
    fun getUserSessions(id: UserId): UserSessions
}
