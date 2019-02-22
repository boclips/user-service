package com.boclips.users.testsupport

import com.boclips.users.domain.model.AccountMetadata
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.identity.IdentityId
import com.boclips.users.domain.service.MetadataProvider

class MetadataProviderFake : MetadataProvider {
    override fun getAllMetadata(ids: List<IdentityId>): Map<IdentityId, AccountMetadata> {
        return ids.map { it to getMetadata(it) }.toMap()
    }

    override fun getMetadata(id: IdentityId): AccountMetadata {
        return AccountMetadata(
            subjects = "some user input",
            analyticsId = AnalyticsId(value = "1112323485912394138924")
        )
    }
}