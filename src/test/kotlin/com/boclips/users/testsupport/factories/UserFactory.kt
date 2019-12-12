package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.Account
import com.boclips.users.domain.model.Profile
import com.boclips.users.domain.model.TeacherPlatformAttributes
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.marketing.MarketingTracking
import com.boclips.users.domain.model.account.OrganisationAccountId
import java.time.ZonedDateTime

class UserFactory {
    companion object {
        fun sample(
            analyticsId: AnalyticsId? = AnalyticsId(
                value = "1234567"
            ),
            referralCode: String? = null,
            marketing: MarketingTracking = MarketingTrackingFactory.sample(),
            account: Account = AccountFactory.sample(),
            profile: Profile? = ProfileFactory.sample(),
            teacherPlatformAttributes: TeacherPlatformAttributes? = TeacherPlatformAttributesFactory.sample(),
            organisationAccountId: OrganisationAccountId? = null,
            accessExpiresOn: ZonedDateTime? = null
        ) = User(
            account = account,
            profile = profile,
            analyticsId = analyticsId,
            referralCode = referralCode,
            teacherPlatformAttributes = teacherPlatformAttributes,
            marketingTracking = marketing,
            organisationAccountId = organisationAccountId,
            accessExpiresOn = accessExpiresOn)

        fun sample(
            id: String
        ) = sample(
            account = AccountFactory.sample(id = id)
        )
    }
}
