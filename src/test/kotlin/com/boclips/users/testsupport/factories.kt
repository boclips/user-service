package com.boclips.users.testsupport

import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.account.Account
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.identity.Identity
import com.boclips.users.presentation.requests.CreateUserRequest
import java.util.UUID

class AccountFactory {
    companion object {
        fun sample(
            id: String = "user-id",
            activated: Boolean = false,
            subjects: String? = "maths english netflix",
            analyticsId: AnalyticsId? = AnalyticsId(value = "1234567"),
            referralCode: String? = null,
            firstName: String = "Joe",
            lastName: String = "Dough",
            email: String = "joe@dough.com"
        ) = Account(
            id = UserId(value = id),
            activated = activated,
            subjects = subjects,
            analyticsId = analyticsId,
            referralCode = referralCode,
            firstName = firstName,
            lastName = lastName,
            email = email
        )
    }
}

class UserIdentityFactory {
    companion object {
        fun sample(
            id: String = UUID.randomUUID().toString(),
            email: String = "test@test.com",
            firstName: String = "Test",
            lastName: String = "Test",
            isVerified: Boolean = true
        ) = Identity(
            id = UserId(value = id),
            email = email,
            firstName = firstName,
            lastName = lastName,
            isVerified = isVerified
        )
    }
}

class CreateUserRequestFactory {
    companion object {
        fun sample(
            firstName: String? = "Hans",
            lastName: String? = "Muster",
            email: String? = "hans@muster.ch",
            password: String? = "heidiisgreat",
            subjects: String? = "argriculture",
            analyticsId: String? = "mixpanel-123",
            referralCode: String? = "referralCode-123"
        ): CreateUserRequest {
            return CreateUserRequest(
                firstName = firstName,
                lastName = lastName,
                email = email,
                password = password,
                subjects = subjects,
                analyticsId = analyticsId,
                referralCode = referralCode
            )
        }
    }
}