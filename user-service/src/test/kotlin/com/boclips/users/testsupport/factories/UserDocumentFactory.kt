package com.boclips.users.testsupport.factories

import com.boclips.users.infrastructure.organisation.OrganisationDocument
import com.boclips.users.infrastructure.user.ExternalIdentityDocument
import com.boclips.users.infrastructure.user.MarketingTrackingDocument
import com.boclips.users.infrastructure.user.UserDocument
import java.time.Instant
import java.util.UUID

class UserDocumentFactory {
    companion object {
        fun sample(
            id: String = UUID.randomUUID().toString(),
            firstName: String? = "Monty",
            lastName: String? = "Python",
            username: String? = "monty@python.com",
            email: String? = "monty@python.com",
            analyticsId: String = "1233",
            referralCode: String? = "code",
            shareCode: String? = "ABC",
            subjects: List<String> = listOf("maths"),
            ageRange: List<Int> = listOf(1, 2),
            hasOptedIntoMarketing: Boolean? = false,
            marketing: MarketingTrackingDocument? = MarketingTrackingDocument(
                utmCampaign = null,
                utmSource = null,
                utmMedium = null,
                utmTerm = null,
                utmContent = null
            ),
            organisation: OrganisationDocument? = null,
            accessExpiresOn: Instant? = null,
            createdAt: Instant = Instant.now(),
            hasLifetimeAccess: Boolean = false,
            role: String = "TEACHER",
            profileSchool: OrganisationDocument? = null,
            externalIdentity: ExternalIdentityDocument? = null
        ): UserDocument = UserDocument(
            _id = id,
            firstName = firstName,
            lastName = lastName,
            analyticsId = analyticsId,
            referralCode = referralCode,
            shareCode = shareCode,
            subjectIds = subjects,
            ageRange = ageRange,
            email = email,
            username = username,
            hasOptedIntoMarketing = hasOptedIntoMarketing,
            marketing = marketing,
            organisation = organisation,
            accessExpiresOn = accessExpiresOn,
            createdAt = createdAt,
            hasLifetimeAccess = hasLifetimeAccess,
            role = role,
            profileSchool = profileSchool,
            externalIdentity = externalIdentity
        )
    }
}
