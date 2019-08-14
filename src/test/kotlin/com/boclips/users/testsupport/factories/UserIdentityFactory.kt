package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.UserSource
import com.boclips.users.domain.model.identity.Identity
import java.util.UUID

class UserIdentityFactory {
    companion object {
        fun sample(
            id: String = UUID.randomUUID().toString(),
            email: String = "test@test.com",
            firstName: String = "Test",
            lastName: String = "Test",
            isVerified: Boolean = true,
            userSource: UserSource = UserSourceFactory.boclipsSample()
        ) = Identity(
            id = UserId(value = id),
            email = email,
            firstName = firstName,
            lastName = lastName,
            isVerified = isVerified,
            associatedTo = userSource
        )
    }
}
