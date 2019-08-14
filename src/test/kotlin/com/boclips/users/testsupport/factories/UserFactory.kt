package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.Subject
import com.boclips.users.domain.model.SubjectId
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.marketing.MarketingTracking
import com.boclips.users.domain.model.organisation.OrganisationId

class UserFactory {
    companion object {
        fun sample(
            id: String = "user-id",
            activated: Boolean = false,
            subjects: List<Subject> = listOf(
                Subject(
                    id = SubjectId(value = "123"),
                    name = "Maths"
                ),
                Subject(
                    id = SubjectId(value = "456"),
                    name = "Netflix"
                )
            ),
            ageRange: List<Int> = listOf(1, 2),
            analyticsId: AnalyticsId? = AnalyticsId(
                value = "1234567"
            ),
            referralCode: String? = null,
            firstName: String = "Joe",
            lastName: String = "Dough",
            email: String = "joe@dough.com",
            hasOptedIntoMarketing: Boolean = true,
            marketing: MarketingTracking = MarketingTrackingFactory.sample(),
            associatedTo: OrganisationId? = null
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
            associatedTo = associatedTo
        )
    }
}
