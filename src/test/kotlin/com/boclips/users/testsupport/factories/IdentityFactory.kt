package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.UserSource
import com.boclips.users.domain.model.identity.Identity
import java.util.UUID

class IdentityFactory {
    companion object {
        fun sample(
            id: String = UUID.randomUUID().toString(),
            email: String = "test@test.com",
            isVerified: Boolean = true,
            userSource: UserSource = UserSourceFactory.boclipsSample()
        ) = Identity(
            id = UserId(value = id),
            email = email,
            isVerified = isVerified,
            associatedTo = userSource
        )
    }
}
