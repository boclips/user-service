package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.UserSource

interface UserSourceResolver {
    fun resolve(roles: List<String>): UserSource?
}
