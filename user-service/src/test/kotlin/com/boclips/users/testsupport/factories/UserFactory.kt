package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.marketing.MarketingTracking
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.user.Identity
import com.boclips.users.domain.model.user.Profile
import com.boclips.users.domain.model.user.TeacherPlatformAttributes
import com.boclips.users.domain.model.user.User
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
            organisation: Organisation? = null,
            shareCode: String? = "DFGY",
            accessExpiresOn: ZonedDateTime? = null
        ) = User(
            identity = identity,
            profile = profile,
            analyticsId = analyticsId,
            referralCode = referralCode,
            teacherPlatformAttributes = teacherPlatformAttributes,
            marketingTracking = marketing,
            organisation = organisation,
            shareCode = shareCode,
            accessExpiresOn = accessExpiresOn
        )

        fun sample(
            id: String
        ) = sample(
            identity = IdentityFactory.sample(id = id)
        )
    }
}
