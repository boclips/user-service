package com.boclips.users.testsupport

import com.boclips.users.domain.model.AccountMetadata
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.service.MetadataProvider

class MetadataProviderFake : MetadataProvider {
    override fun getAllMetadata(ids: List<UserId>): Map<UserId, AccountMetadata> {
        return ids.map { it to getMetadata(it) }.toMap()
    }

    override fun getMetadata(id: UserId): AccountMetadata {
        return AccountMetadata(
            subjects = "some user input",
            analyticsId = AnalyticsId(value = "1112323485912394138924")
        )
    }
}