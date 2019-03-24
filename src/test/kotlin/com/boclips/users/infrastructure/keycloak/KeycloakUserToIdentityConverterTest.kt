package com.boclips.users.infrastructure.keycloak

import com.boclips.users.infrastructure.keycloak.client.KeycloakUserToUserIdentityConverter
import com.boclips.users.infrastructure.keycloak.client.exceptions.InvalidUserRepresentation
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.keycloak.representations.idm.UserRepresentation
import java.util.UUID

class KeycloakUserToIdentityConverterTest {
    private val userConverter = KeycloakUserToUserIdentityConverter()

    lateinit var keycloakUser: UserRepresentation

    @BeforeEach
    fun setup() {
        keycloakUser = UserRepresentation().apply {
            this.id = UUID.randomUUID().toString()
            this.email = "test@gmail.com"
            this.firstName = "Shane"
            this.lastName = "Williams"
            this.isEmailVerified = true
        }
    }

    @Test
    fun `converts correctly when all fields are valid`() {
        val convertedUser = userConverter.convert(keycloakUser)

        assertThat(convertedUser.id.value).isEqualTo(keycloakUser.id)
        assertThat(convertedUser.email).isEqualTo(keycloakUser.email)
        assertThat(convertedUser.firstName).isEqualTo(keycloakUser.firstName)
        assertThat(convertedUser.lastName).isEqualTo(keycloakUser.lastName)
        assertThat(convertedUser.isVerified).isEqualTo(keycloakUser.isEmailVerified)
    }

    @Test
    fun `throws when id is null`() {
        keycloakUser.id = null

        assertThatThrownBy { userConverter.convert(keycloakUser) }
            .isInstanceOf(InvalidUserRepresentation::class.java)
            .hasMessage("missing id")
    }

    @Test
    fun `throws when firstname is null`() {
        keycloakUser.firstName = null

        assertThatThrownBy { userConverter.convert(keycloakUser) }
            .isInstanceOf(InvalidUserRepresentation::class.java)
            .hasMessage("missing firstName")
    }

    @Test
    fun `throws when lastname is null`() {
        keycloakUser.lastName = null

        assertThatThrownBy { userConverter.convert(keycloakUser) }
            .isInstanceOf(InvalidUserRepresentation::class.java)
            .hasMessage("missing lastName")
    }

    @Test
    fun `throws when id is empty string`() {
        keycloakUser.id = ""

        assertThatThrownBy { userConverter.convert(keycloakUser) }
            .isInstanceOf(InvalidUserRepresentation::class.java)
            .hasMessage("missing id")
    }

    @Test
    fun `throws when firstname is empty string`() {
        keycloakUser.firstName = ""

        assertThatThrownBy { userConverter.convert(keycloakUser) }
            .isInstanceOf(InvalidUserRepresentation::class.java)
            .hasMessage("missing firstName")
    }

    @Test
    fun `throws when lastname is empty string`() {
        keycloakUser.lastName = ""

        assertThatThrownBy { userConverter.convert(keycloakUser) }
            .isInstanceOf(InvalidUserRepresentation::class.java)
            .hasMessage("missing lastName")
    }

    @Test
    fun `throws when email is empty string`() {
        keycloakUser.email = ""

        assertThatThrownBy { userConverter.convert(keycloakUser) }
            .isInstanceOf(InvalidUserRepresentation::class.java)
            .hasMessage("invalid email")
    }

    @Test
    fun `throws when email is invalid`() {
        keycloakUser.email = "invalid email"

        assertThatThrownBy { userConverter.convert(keycloakUser) }
            .isInstanceOf(InvalidUserRepresentation::class.java)
            .hasMessage("invalid email")
    }
}