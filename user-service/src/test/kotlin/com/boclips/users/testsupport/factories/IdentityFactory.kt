package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.Identity
import com.boclips.users.domain.model.UserId
import java.time.ZonedDateTime

class IdentityFactory {
    companion object {
        fun sample(
            id: String = UserId().value,
            username: String = "joe@dough.com",
            roles: List<String> = emptyList(),
            createdAt: ZonedDateTime = ZonedDateTime.now()
        ) = Identity(
            id = UserId(value = id),
            username = username,
            roles = roles,
            createdAt = createdAt
        )
    }
}
