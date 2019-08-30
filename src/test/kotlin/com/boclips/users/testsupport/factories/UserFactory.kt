package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.Account
import com.boclips.users.domain.model.Profile
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.marketing.MarketingTracking
import com.boclips.users.domain.model.organisation.OrganisationId

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
            organisationId: OrganisationId? = null
        ) = User(
            account = account,
            profile = profile,
            analyticsId = analyticsId,
            referralCode = referralCode,
            marketingTracking = marketing,
            organisationId = organisationId
        )

        fun sample(
            id: String
        ) = sample(
            account = AccountFactory.sample(id = id)
        )
    }
}
