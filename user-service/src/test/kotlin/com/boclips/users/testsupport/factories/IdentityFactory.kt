package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.user.Identity
import com.boclips.users.domain.model.user.UserId
import java.time.ZonedDateTime

class IdentityFactory {
    companion object {
        fun sample(
            id: String = UserId().value,
            username: String = "joe@dough.com",
            firstName: String = "FirstName",
            idpEmail: String = "test@boclips.com",
            roles: List<String> = emptyList(),
            createdAt: ZonedDateTime = ZonedDateTime.now()
        ) = Identity(
            id = UserId(value = id),
            username = username,
            firstName = firstName,
            idpEmail = idpEmail,
            roles = roles,
            createdAt = createdAt
        )
    }
}
