package com.boclips.users.testsupport

import com.boclips.users.domain.model.Subject
import com.boclips.users.domain.model.SubjectId
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.identity.Identity
import com.boclips.users.domain.model.marketing.CrmProfile
import com.boclips.users.domain.model.marketing.MarketingTracking
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.infrastructure.user.MarketingTrackingDocument
import com.boclips.users.infrastructure.user.OrganisationDocument
import com.boclips.users.infrastructure.user.UserDocument
import com.boclips.users.presentation.requests.CreateUserRequest
import org.bson.types.ObjectId
import java.time.Instant
import java.util.UUID

class AccountFactory {
    companion object {
        fun sample(
            id: String = "user-id",
            activated: Boolean = false,
            subjects: List<Subject> = listOf(
                Subject(id = SubjectId(value = "123"), name = "Maths"),
                Subject(id = SubjectId(value = "456"), name = "Netflix")
            ),
            ageRange: List<Int> = listOf(1, 2),
            analyticsId: AnalyticsId? = AnalyticsId(value = "1234567"),
            referralCode: String? = null,
            firstName: String = "Joe",
            lastName: String = "Dough",
            email: String = "joe@dough.com",
            hasOptedIntoMarketing: Boolean = true,
            marketing: MarketingTracking = MarketingTrackingFactory.sample(),
            organisationId: OrganisationId? = null
        ) = User(
            id = UserId(value = id),
            activated = activated,
            analyticsId = analyticsId,
            subjects = subjects,
            ageRange = ageRange,
            referralCode = referralCode,
            firstName = firstName,
            lastName = lastName,
            email = email,
            hasOptedIntoMarketing = hasOptedIntoMarketing,
            marketingTracking = marketing,
            organisationId = organisationId
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
            ageRange: List<Int>? = listOf(1, 2, 4),
            referralCode: String? = "referralCode-123",
            recaptchaToken: String? = "03AOLTBLRK4xnVft-qESRgTGxK_4WAE...",
            hasOptedIntoMarketing: Boolean? = false,
            analyticsId: String? = "mixpanel-123",
            utmCampaign: String? = null,
            utmContent: String? = null,
            utmMedium: String? = null,
            utmSource: String? = null,
            utmTerm: String? = null
        ): CreateUserRequest {
            return CreateUserRequest(
                firstName = firstName,
                lastName = lastName,
                email = email,
                password = password,
                subjects = subjects,
                ageRange = ageRange,
                analyticsId = analyticsId,
                referralCode = referralCode,
                hasOptedIntoMarketing = hasOptedIntoMarketing,
                recaptchaToken = recaptchaToken,
                utmCampaign = utmCampaign,
                utmContent = utmContent,
                utmMedium = utmMedium,
                utmSource = utmSource,
                utmTerm = utmTerm
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
            ageRange: List<Int> = listOf(1, 2),
            hasOptedIntoMarketing: Boolean? = false,
            organisationId: String? = null
        ): UserDocument = UserDocument(
            id = id,
            firstName = firstName,
            lastName = lastName,
            activated = activated,
            analyticsId = analyticsId,
            referralCode = referralCode,
            subjectIds = subjects,
            ageRange = ageRange,
            email = email,
            hasOptedIntoMarketing = hasOptedIntoMarketing,
            marketing = MarketingTrackingDocument(
                utmCampaign = null,
                utmSource = null,
                utmMedium = null,
                utmTerm = null,
                utmContent = null
            ),
            organisationId = organisationId
        )
    }
}

class OrganisationIdFactory {
    companion object {
        fun sample(
            id: String = UUID.randomUUID().toString()
        ) = OrganisationId(value = id)
    }
}

class OrganisationDocumentFactory {
    companion object {
        fun sample(
            name: String = "The Best Organisation"
        ) = OrganisationDocument(ObjectId(), name)
    }
}

class MarketingTrackingFactory {
    companion object {
        fun sample(
            utmTerm: String = "",
            utmContent: String = "",
            utmMedium: String = "",
            utmSource: String = "",
            utmCampaign: String = ""
        ): MarketingTracking {
            return MarketingTracking(
                utmTerm = utmTerm,
                utmContent = utmContent,
                utmMedium = utmMedium,
                utmSource = utmSource,
                utmCampaign = utmCampaign
            )
        }
    }
}

class CrmProfileFactory {
    companion object {
        fun sample(
            ageRanges: List<Int> = emptyList(),
            subjects: List<Subject> = emptyList(),
            pointInTime: Instant? = Instant.now()
        ): CrmProfile {
            return CrmProfile(
                id = UserId(value = "some-id"),
                activated = true,
                subjects = subjects,
                ageRange = ageRanges,
                firstName = "",
                lastName = "",
                lastLoggedIn = pointInTime,
                email = "email@internet.com",
                hasOptedIntoMarketing = true,
                marketingTracking = MarketingTrackingFactory.sample()
            )
        }
    }
}