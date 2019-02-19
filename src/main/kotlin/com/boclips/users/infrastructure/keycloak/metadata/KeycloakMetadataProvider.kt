package com.boclips.users.infrastructure.keycloak.metadata

import com.boclips.users.domain.model.AccountMetadata
import com.boclips.users.domain.model.analytics.MixpanelId
import com.boclips.users.domain.model.identity.IdentityId
import com.boclips.users.domain.service.MetadataProvider
import com.boclips.users.infrastructure.keycloak.client.KeycloakClient
import org.keycloak.admin.client.Keycloak

class KeycloakMetadataProvider(private val keycloakInstance: Keycloak) : MetadataProvider {
    override fun getAllMetadata(ids: List<IdentityId>): Map<IdentityId, AccountMetadata> {
        val userCount = keycloakInstance.realm(KeycloakClient.REALM).users().count()

        return keycloakInstance
            .realm(KeycloakClient.REALM)
            .users()
            .list(0, userCount)
            .mapNotNull {
                if (it.attributes == null) {
                    null
                } else if (it.attributes["subjects"] == null || it.attributes["mixpanelDistinctId"] == null) {
                    null
                } else {
                    val subjects = it.attributes["subjects"]?.first()
                    val mixpanelId = it.attributes["mixpanelDistinctId"]?.first()

                    IdentityId(value = it.id) to AccountMetadata(
                        subjects = subjects!!,
                        mixpanelId = MixpanelId(value = mixpanelId!!)
                    )
                }
            }.toMap()
    }

    override fun getMetadata(id: IdentityId): AccountMetadata {
        val attributes =
            keycloakInstance.realm(KeycloakClient.REALM).users().get(id.value).toRepresentation().attributes

        val subjects = attributes.get("subjects")?.first()
        val mixpanelId = attributes.get("mixpanelId")?.first()

        return AccountMetadata(subjects = subjects!!, mixpanelId = MixpanelId(value = mixpanelId!!))
    }
}
