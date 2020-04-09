package com.boclips.users.domain.service

import org.bson.types.ObjectId

object UniqueId {

    operator fun invoke(): String {
        return ObjectId().toHexString()
    }
}
