package com.boclips.users.infrastructure.keycloakclient

import com.boclips.users.domain.model.AccountMetadata
import com.boclips.users.domain.model.analytics.MixpanelId
import com.boclips.users.domain.model.identity.IdentityId
import com.boclips.users.domain.service.MetadataProvider
import org.keycloak.admin.client.Keycloak

class KeycloakMetadataProvider(private val keycloakInstance: Keycloak) : MetadataProvider {
    override fun getMetadata(id: IdentityId): AccountMetadata {
        val attributes =
            keycloakInstance.realm(KeycloakClient.REALM).users().get(id.value).toRepresentation().attributes

        val subjects = attributes.get("subjects")?.first()
        val mixpanelId = attributes.get("mixpanelId")?.first()

        return AccountMetadata(subjects = subjects!!, mixpanelId = MixpanelId(value = mixpanelId!!))
    }
}
