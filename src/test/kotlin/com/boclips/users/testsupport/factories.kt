package com.boclips.users.testsupport

import com.boclips.users.domain.model.account.Account
import com.boclips.users.domain.model.account.AccountId
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.identity.Identity
import com.boclips.users.domain.model.identity.IdentityId
import java.util.UUID

class AccountFactory {
    companion object {
        fun sample(
            id: String = "user-id",
            activated: Boolean = false,
            subjects: String? = "maths english netflix",
            analyticsId: AnalyticsId? = AnalyticsId(value = "1234567"),
            isReferral: Boolean = false
        ) = Account(
            id = AccountId(value = id),
            activated = activated,
            subjects = subjects,
            analyticsId = analyticsId,
            isReferral = isReferral
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
            id = IdentityId(value = id),
            email = email,
            firstName = firstName,
            lastName = lastName,
            isEmailVerified = isVerified
        )
    }
}
