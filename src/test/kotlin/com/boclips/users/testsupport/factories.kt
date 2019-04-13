package com.boclips.users.testsupport

import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.identity.Identity
import com.boclips.users.infrastructure.user.UserDocument
import com.boclips.users.presentation.requests.CreateUserRequest
import java.util.UUID

class AccountFactory {
    companion object {
        fun sample(
            id: String = "user-id",
            activated: Boolean = false,
            subjects: List<String> = listOf("maths-1", "english-2", "netflix-3"),
            analyticsId: AnalyticsId? = AnalyticsId(value = "1234567"),
            referralCode: String? = null,
            firstName: String = "Joe",
            lastName: String = "Dough",
            email: String = "joe@dough.com",
            hasOptedIntoMarketing: Boolean = true
        ) = User(
            id = UserId(value = id),
            activated = activated,
            analyticsId = analyticsId,
            subjects = subjects,
            referralCode = referralCode,
            firstName = firstName,
            lastName = lastName,
            email = email,
            hasOptedIntoMarketing = hasOptedIntoMarketing
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
            subjects: List<String>? = listOf("argriculture"),
            analyticsId: String? = "mixpanel-123",
            referralCode: String? = "referralCode-123",
            recaptchaToken: String? = "03AOLTBLRK4xnVft-qESRgTGxK_4WAE...",
            hasOptedIntoMarketing: Boolean? = false
        ): CreateUserRequest {
            return CreateUserRequest(
                firstName = firstName,
                lastName = lastName,
                email = email,
                password = password,
                subjects = subjects,
                analyticsId = analyticsId,
                referralCode = referralCode,
                hasOptedIntoMarketing = hasOptedIntoMarketing,
                recaptchaToken = recaptchaToken
            )
        }
    }
}

class UserDocumentFactory {
    companion object {
        fun sample(
            id: String = UUID.randomUUID().toString(),
            firstName: String? = "Monty",
            lastName: String? = "Python",
            email: String? = "monty@python.com",
            activated: Boolean = false,
            analyticsId: String = "1233",
            referralCode: String? = "code",
            subjects: List<String> = listOf("maths"),
            hasOptedIntoMarketing: Boolean? = false
        ): UserDocument = UserDocument(
            id = id,
            firstName = firstName,
            lastName = lastName,
            activated = activated,
            analyticsId = analyticsId,
            referralCode = referralCode,
            subjectIds = subjects,
            email = email,
            hasOptedIntoMarketing = hasOptedIntoMarketing
        )
    }
}