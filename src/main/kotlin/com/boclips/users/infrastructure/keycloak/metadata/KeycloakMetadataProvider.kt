package com.boclips.users.infrastructure.keycloak.metadata

import com.boclips.users.domain.model.AccountMetadata
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.identity.IdentityId
import com.boclips.users.domain.service.MetadataProvider
import com.boclips.users.infrastructure.keycloak.KeycloakWrapper

class KeycloakMetadataProvider(private val keycloakWrapper: KeycloakWrapper) : MetadataProvider {
    override fun getAllMetadata(ids: List<IdentityId>): Map<IdentityId, AccountMetadata> {

        return keycloakWrapper.users()
            .mapNotNull {
                if (it.attributes == null) {
                    null
                } else {
                    IdentityId(value = it.id) to toAccountMetadata(it.attributes)
                }
            }.toMap()
    }

    override fun getMetadata(id: IdentityId): AccountMetadata {
        val attributes = keycloakWrapper.getUser(id.value)?.attributes
            ?: return AccountMetadata(null, null)

        return toAccountMetadata(attributes)
    }

    private fun toAccountMetadata(attributes: Map<String, List<String>?>): AccountMetadata {
        val subjects = attributes.get("subjects")?.first()
        val mixpanelId = attributes.get("mixpanelDistinctId")?.first()

        return AccountMetadata(
            subjects = subjects,
            analyticsId = getAnalyticsIdSafely(mixpanelId)
        )
    }

    private fun getAnalyticsIdSafely(id: String?): AnalyticsId? = id?.let { AnalyticsId(value = it) }
}
