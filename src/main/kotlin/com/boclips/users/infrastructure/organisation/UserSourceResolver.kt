package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.Platform

interface UserSourceResolver {
    fun resolve(roles: List<String>): Platform?
}
