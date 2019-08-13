package com.boclips.users.infrastructure.keycloak.client

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.keycloak.representations.idm.UserRepresentation

internal class KeycloakUserToUserIdentityConverterTest {

    @Test
    fun `convert keycloak user representation to identity`() {
        val identity = KeycloakUserToUserIdentityConverter().convert(UserRepresentation().apply {
            id = "id-123"
            realmRoles = listOf("SOME_ROLE")
            firstName = "Zoe"
            lastName = "Alcapone"
            email = "alcapone@padrino.it"
            isEmailVerified = true
        })

        assertThat(identity.id.value).isEqualTo("id-123")
        assertThat(identity.firstName).isEqualTo("Zoe")
        assertThat(identity.lastName).isEqualTo("Alcapone")
        assertThat(identity.email).isEqualTo("alcapone@padrino.it")
        assertThat(identity.roles).containsExactly("SOME_ROLE")
        assertThat(identity.isVerified).isEqualTo(true)
    }
}
