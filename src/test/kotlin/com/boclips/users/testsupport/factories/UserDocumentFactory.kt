package com.boclips.users.testsupport.factories

import com.boclips.users.infrastructure.user.MarketingTrackingDocument
import com.boclips.users.infrastructure.user.UserDocument
import java.util.UUID

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