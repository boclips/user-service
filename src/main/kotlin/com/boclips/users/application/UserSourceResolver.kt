package com.boclips.users.application

import com.boclips.users.domain.model.UserSource

interface UserSourceResolver {
    fun resolve(roles: List<String>): UserSource?
}
