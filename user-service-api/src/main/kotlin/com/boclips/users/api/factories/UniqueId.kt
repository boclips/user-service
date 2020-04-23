package com.boclips.users.api.factories

import java.util.UUID

object UniqueId {

    operator fun invoke(): String {
        return UUID.randomUUID().toString()
    }
}
