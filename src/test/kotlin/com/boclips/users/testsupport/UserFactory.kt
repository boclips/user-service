package com.boclips.users.testsupport

import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.account.Account
import com.boclips.users.domain.model.analytics.MixpanelId
import com.boclips.users.domain.model.identity.Identity

class UserFactory {
    companion object {
        fun sample(
            userId: UserId = UserId(value = "user-id"),
            account: Account = AccountFactory.sample(),
            identity: Identity = UserIdentityFactory.sample(),
            analyticsId: MixpanelId = MixpanelId(value = "mixpanel-id"),
            subjects: String = "maths english, sports"
        ) = User(
            account = account,
            userId = userId,
            identity = identity,
            analyticsId = analyticsId,
            subjects = subjects
        )
    }
}
