package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.user.Identity
import com.boclips.users.domain.model.user.UserId
import java.time.ZonedDateTime

class IdentityFactory {
    companion object {
        fun sample(
            id: String = UserId().value,
            username: String = "joe@dough.com",
            roles: List<String> = emptyList(),
            createdAt: ZonedDateTime = ZonedDateTime.now(),
            legacyOrganisationId: String? = null
        ) = Identity(
            id = UserId(value = id),
            username = username,
            roles = roles,
            createdAt = createdAt,
            legacyOrganisationId = legacyOrganisationId
        )
    }
}
