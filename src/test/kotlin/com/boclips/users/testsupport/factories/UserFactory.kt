package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.Account
import com.boclips.users.domain.model.Profile
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.marketing.MarketingTracking
import com.boclips.users.domain.model.organisation.OrganisationAccountId
import java.time.ZonedDateTime

class UserFactory {
    companion object {
        fun sample(
            analyticsId: AnalyticsId? = AnalyticsId(
                value = "1234567"
            ),
            referralCode: String? = null,
            shareCode: String = "ABC",
            marketing: MarketingTracking = MarketingTrackingFactory.sample(),
            account: Account = AccountFactory.sample(),
            profile: Profile? = ProfileFactory.sample(),
            organisationAccountId: OrganisationAccountId? = null,
            accessExpiresOn: ZonedDateTime? = null,
            hasLifetimeAccess: Boolean = false
        ) = User(
            account = account,
            profile = profile,
            analyticsId = analyticsId,
            referralCode = referralCode,
            shareCode = shareCode,
            marketingTracking = marketing,
            organisationAccountId = organisationAccountId,
            accessExpiresOn = accessExpiresOn,
            hasLifetimeAccess = hasLifetimeAccess
        )

        fun sample(
            id: String
        ) = sample(
            account = AccountFactory.sample(id = id)
        )
    }
}
