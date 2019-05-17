package com.boclips.users.infrastructure.keycloak

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.keycloak.admin.client.Keycloak
import org.keycloak.representations.idm.UserRepresentation
import org.keycloak.representations.idm.UserSessionRepresentation
import org.springframework.util.ResourceUtils
import org.yaml.snakeyaml.Yaml
import java.io.InputStream
import java.util.UUID

class KeycloakWrapperContractTest {
    private val keycloakInstance: Keycloak = Keycloak.getInstance(
        "https://login.testing-boclips.com/auth",
        KeycloakWrapper.REALM,
        readSecret("KEYCLOAK_USERNAME"),
        readSecret("KEYCLOAK_PASSWORD"),
        "admin-cli"
    )

    private fun readSecret(key: String): String {
        if (System.getenv(key) != null) {
            return System.getenv(key)
        }

        val yaml = Yaml()
        val inputStream: InputStream = ResourceUtils.getFile("classpath:contract-test-setup.yml").inputStream()

        val apiKey = yaml.load<Map<String, String>>(inputStream)[key]!!
        inputStream.close()

        return apiKey
    }

    @Test
    fun `can create and delete a user`() {
        val wrapper = KeycloakWrapper(keycloakInstance)

        val randomUsername = UUID.randomUUID().toString()

        val createdUser = wrapper.createUser(
            KeycloakUser(
                firstName = "Hans",
                lastName = "Muster",
                email = "ben+$randomUsername@boclips.com",
                password = "123"
            )
        )

        assertThat(createdUser.id).isNotNull()
        assertThat(createdUser.firstName).isEqualTo("Hans")
        assertThat(createdUser.lastName).isEqualTo("Muster")
        assertThat(createdUser.username).isEqualTo("ben+$randomUsername@boclips.com")
        assertThat(createdUser.email).isEqualTo("ben+$randomUsername@boclips.com")

        wrapper.removeUser(createdUser.id)
    }

    @Test
    fun `throws when user already exists`() {
        val wrapper = KeycloakWrapper(keycloakInstance)

        val randomUsername = UUID.randomUUID().toString()

        val user = KeycloakUser(
            firstName = "Hans",
            lastName = "Muster",
            email = "ben+$randomUsername@boclips.com",
            password = "123"
        )

        assertThrows<UserAlreadyExistsException> {
            wrapper.createUser(user)
            wrapper.createUser(user)
        }

        wrapper.removeUser(user.email)
    }

    @Test
    fun `can fetch users`() {
        val wrapper = KeycloakWrapper(keycloakInstance)

        val users: List<UserRepresentation> = wrapper.users()

        assertThat(users.size).isGreaterThan(1)
    }

    @Nested
    @DisplayName("Testing sessions and last login")
    inner class Sessions {
        @Test
        fun `cannot fetch user session for a user that has never logged in`() {
            val wrapper = KeycloakWrapper(keycloakInstance)

            val aUser = wrapper.users().first()
            val userSessionRepresentation = wrapper.getLastUserSession(aUser.id)

            assertThat(userSessionRepresentation).isNull()
        }

        @Test
        fun `can fetch user session for a user that has logged in`() {
            val wrapper = KeycloakWrapper(keycloakInstance)

            val aUser = wrapper.getUserByUsername("user-service@boclips.com")
            val userSessionRepresentation: UserSessionRepresentation? = wrapper.getLastUserSession(aUser!!.id)

            assertThat(userSessionRepresentation).isNotNull
            assertThat(userSessionRepresentation!!.lastAccess).isGreaterThan(1558080047000)
        }
    }

    @Test
    fun `can count users`() {
        val wrapper = KeycloakWrapper(keycloakInstance)

        val count = wrapper.countUsers()

        assertThat(count).isGreaterThan(1)
    }

    @Nested
    inner class GetUserTest {
        @Test
        fun `returns null when user does not exist`() {
            val wrapper = KeycloakWrapper(keycloakInstance)

            val user: UserRepresentation? = wrapper.getUser("4567890")

            assertThat(user).isNull()
        }

        @Test
        fun `returns user`() {
            val wrapper = KeycloakWrapper(keycloakInstance)
            val aUser = wrapper.users().first()

            val user: UserRepresentation = wrapper.getUser(aUser.id)!!

            assertThat(user.id).isEqualTo(aUser.id)
        }
    }

    @Nested
    inner class GetUserByUsernameTest {
        @Test
        fun `gets user by username`() {
            val wrapper = KeycloakWrapper(keycloakInstance)

            val aUser = wrapper.users().first()

            val user: UserRepresentation = wrapper.getUserByUsername(aUser.username)!!

            assertThat(user.id).isEqualTo(aUser.id)
        }

        @Test
        fun `returns null for non-existant username`() {
            val wrapper = KeycloakWrapper(keycloakInstance)

            val user: UserRepresentation? = wrapper.getUserByUsername("this should not exist")

            assertThat(user).isNull()
        }
    }
}