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
            username: String? = "monty@python.com",
            email: String? = "monty@python.com",
            analyticsId: String = "1233",
            referralCode: String? = "code",
            subjects: List<String> = listOf("maths"),
            ageRange: List<Int> = listOf(1, 2),
            hasOptedIntoMarketing: Boolean? = false,
            country: String? = "United States of America",
            state: String? = "New York",
            school: String? = "Brooklyn School"
            ): UserDocument = UserDocument(
            id = id,
            firstName = firstName,
            lastName = lastName,
            analyticsId = analyticsId,
            referralCode = referralCode,
            subjectIds = subjects,
            ageRange = ageRange,
            email = email,
            username = username,
            hasOptedIntoMarketing = hasOptedIntoMarketing,
            marketing = MarketingTrackingDocument(
                utmCampaign = null,
                utmSource = null,
                utmMedium = null,
                utmTerm = null,
                utmContent = null
            ),
            country = country,
            state = state,
            school = school,
            organisationId = "some-org-id"
        )
    }
}
