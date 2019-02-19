package com.boclips.users.testsupport

import com.boclips.users.domain.model.AccountMetadata
import com.boclips.users.domain.model.analytics.MixpanelId
import com.boclips.users.domain.model.identity.IdentityId
import com.boclips.users.domain.service.MetadataProvider

class MetadataProviderFake : MetadataProvider {
    override fun getMetadata(id: IdentityId): AccountMetadata {
        return AccountMetadata(
            subjects = "some user input",
            mixpanelId = MixpanelId(value = "1112323485912394138924")
        )
    }
}