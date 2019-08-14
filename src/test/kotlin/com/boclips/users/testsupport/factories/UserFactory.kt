package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.Subject
import com.boclips.users.domain.model.SubjectId
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.UserSource
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.marketing.MarketingTracking

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
            ages: List<Int> = listOf(1, 2),
            analyticsId: AnalyticsId? = AnalyticsId(
                value = "1234567"
            ),
            referralCode: String? = null,
            firstName: String = "Joe",
            lastName: String = "Dough",
            email: String = "joe@dough.com",
            hasOptedIntoMarketing: Boolean = true,
            marketing: MarketingTracking = MarketingTrackingFactory.sample(),
            userSource: UserSource = UserSource.Boclips
        ) = User(
            id = UserId(value = id),
            activated = activated,
            analyticsId = analyticsId,
            subjects = subjects,
            ages = ages,
            referralCode = referralCode,
            firstName = firstName,
            lastName = lastName,
            email = email,
            hasOptedIntoMarketing = hasOptedIntoMarketing,
            marketingTracking = marketing,
            associatedTo = userSource
        )
    }
}
