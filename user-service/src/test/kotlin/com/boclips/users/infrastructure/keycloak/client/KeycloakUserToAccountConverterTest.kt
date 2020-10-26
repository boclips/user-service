package com.boclips.users.infrastructure.keycloak.client

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.keycloak.representations.idm.UserRepresentation
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.UUID

class KeycloakUserToAccountConverterTest {
    lateinit var keycloakUser: UserRepresentation
    lateinit var userConverter: KeycloakUserToAccountConverter

    @BeforeEach
    fun setup() {
        userConverter = KeycloakUserToAccountConverter()
        keycloakUser = UserRepresentation().apply {
            this.id = UUID.randomUUID().toString()
            this.username = "test@gmail.com"
            this.isEmailVerified = true
            this.realmRoles = listOf("ROLE_VIEWSONIC", "ROLE_TEACHER", "ROLE_HQ", "uma_something")
            this.createdTimestamp = Instant.now().toEpochMilli()
        }
    }

    @Test
    fun `converts correctly when all fields are valid`() {
        val convertedUser = userConverter.convert(keycloakUser)

        assertThat(convertedUser.id.value).isEqualTo(keycloakUser.id)
        assertThat(convertedUser.username).isEqualTo(keycloakUser.username)
        assertThat(convertedUser.roles).containsExactly(
            "ROLE_VIEWSONIC",
            "ROLE_TEACHER",
            "ROLE_HQ",
            "uma_something"
        )
        assertThat(convertedUser.createdAt).isEqualTo(
            ZonedDateTime.ofInstant(
                Instant.ofEpochMilli(keycloakUser.createdTimestamp),
                ZoneOffset.UTC
            )
        )
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
    fun `throws should username ever be null`() {
        keycloakUser.username = null

        assertThatThrownBy { userConverter.convert(keycloakUser) }
            .isInstanceOf(IllegalStateException::class.java)
    }

    @Test
    fun `use email if username is not a valid email`() {
        val user = UserRepresentation().apply {
            this.id = UUID.randomUUID().toString()
            this.email = "test@gmail.com"
            this.username = "somethingnotvalidemail"
            this.isEmailVerified = true
            this.realmRoles = listOf("ROLE_VIEWSONIC", "ROLE_TEACHER", "ROLE_HQ", "uma_something")
            this.createdTimestamp = Instant.now().toEpochMilli()
        }

        val convertedUser = userConverter.convert(user)

        assertThat(convertedUser.email).isEqualTo(user.email)
        assertThat(convertedUser.username).isEqualTo(user.username)
    }

    @Test
    fun `username takes precedence over email if username is valid email`() {
        val user = UserRepresentation().apply {
            this.id = UUID.randomUUID().toString()
            this.email = "ignorethis@gmail.com"
            this.username = "test@gmail.com"
            this.isEmailVerified = true
            this.realmRoles = listOf("ROLE_VIEWSONIC", "ROLE_TEACHER", "ROLE_HQ", "uma_something")
            this.createdTimestamp = Instant.now().toEpochMilli()
        }

        val convertedUser = userConverter.convert(user)

        assertThat(convertedUser.email).isEqualTo(user.username)
        assertThat(convertedUser.username).isEqualTo(user.username)
    }
}
