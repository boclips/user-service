package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.Identity
import com.boclips.users.domain.model.Profile
import com.boclips.users.domain.model.TeacherPlatformAttributes
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.marketing.MarketingTracking
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.School
import java.time.ZonedDateTime

class UserFactory {
    companion object {
        fun sample(
            analyticsId: AnalyticsId? = AnalyticsId(
                value = "1234567"
            ),
            referralCode: String? = null,
            marketing: MarketingTracking = MarketingTrackingFactory.sample(),
            identity: Identity = IdentityFactory.sample(),
            profile: Profile? = ProfileFactory.sample(),
            teacherPlatformAttributes: TeacherPlatformAttributes? = TeacherPlatformAttributesFactory.sample(),
            organisation: Organisation<*>? = null,
            accessExpiresOn: ZonedDateTime? = null
        ) = User(
            identity = identity,
            profile = profile,
            analyticsId = analyticsId,
            referralCode = referralCode,
            teacherPlatformAttributes = teacherPlatformAttributes,
            marketingTracking = marketing,
            organisation = organisation,
            accessExpiresOn = accessExpiresOn
        )

        fun sample(
            id: String
        ) = sample(
            identity = IdentityFactory.sample(id = id)
        )
    }
}
