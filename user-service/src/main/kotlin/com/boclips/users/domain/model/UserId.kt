package com.boclips.users.domain.model

import com.boclips.users.domain.service.UniqueId

data class UserId(val value: String) {
    companion object {
        operator fun invoke(): UserId {
            return UserId(UniqueId())
        }
    }
}
