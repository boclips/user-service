package com.boclips.users.infrastructure.keycloak.client

import com.boclips.users.infrastructure.keycloak.client.exceptions.InvalidUserRepresentation
import com.boclips.users.infrastructure.organisation.UserSourceResolver
import com.boclips.users.testsupport.factories.UserSourceFactory
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.keycloak.representations.idm.UserRepresentation
import java.util.UUID

class KeycloakUserToIdentityConverterTest {
    lateinit var keycloakUser: UserRepresentation
    lateinit var userConverter: KeycloakUserToIdentityConverter

    @BeforeEach
    fun setup() {
        val organisationMatcherMock = mock<UserSourceResolver>()
        whenever(organisationMatcherMock.resolve(any())).thenReturn(UserSourceFactory.boclipsSample())

        userConverter = KeycloakUserToIdentityConverter(organisationMatcherMock)
        keycloakUser = UserRepresentation().apply {
            this.id = UUID.randomUUID().toString()
            this.email = "test@gmail.com"
            this.firstName = "Shane"
            this.lastName = "Williams"
            this.isEmailVerified = true
            this.realmRoles = listOf("ROLE_VIEWSONIC", "ROLE_TEACHER", "ROLE_BACKOFFICE", "uma_something")
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
        assertThat(convertedUser.associatedTo).isNotNull()
    }

    @Test
    fun `throws should id ever be null`() {
        keycloakUser.id = null

        assertThatThrownBy { userConverter.convert(keycloakUser) }
            .isInstanceOf(IllegalStateException::class.java)
    }

    @Test
    fun `throws when id is empty string`() {
        keycloakUser.id = ""

        assertThatThrownBy { userConverter.convert(keycloakUser) }
            .isInstanceOf(IllegalStateException::class.java)
    }

    @Test
    fun `can deal with null first name`() {
        keycloakUser.firstName = null

        assertThat(userConverter.convert(keycloakUser).firstName).isEmpty()
    }

    @Test
    fun `can deal with empty first name`() {
        keycloakUser.firstName = ""

        assertThat(userConverter.convert(keycloakUser).firstName).isEmpty()
    }

    @Test
    fun `can deal with null last name`() {
        keycloakUser.lastName = null

        assertThat(userConverter.convert(keycloakUser).lastName).isEmpty()
    }

    @Test
    fun `can deal with empty last name`() {
        keycloakUser.lastName = ""

        assertThat(userConverter.convert(keycloakUser).lastName).isEmpty()
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
