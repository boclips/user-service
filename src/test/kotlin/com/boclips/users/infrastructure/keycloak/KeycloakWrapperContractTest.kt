package com.boclips.users.infrastructure.keycloak

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.keycloak.admin.client.Keycloak
import org.keycloak.representations.idm.UserRepresentation
import org.springframework.util.ResourceUtils
import org.yaml.snakeyaml.Yaml
import java.io.InputStream
import java.time.LocalDate
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

    @Nested
    inner class Events {
        @Test
        fun `gets REGISTER events`() {
            val wrapper = KeycloakWrapper(keycloakInstance)

            val events = wrapper.getRegisterEvents(LocalDate.now().minusDays(100))

            assertThat(events.size).isGreaterThanOrEqualTo(1)
        }
    }

    @Nested
    inner class Groups {
        @Test
        fun `can the groups of a user`() {
            val wrapper = KeycloakWrapper(keycloakInstance)

            val aUser = wrapper.users().first()
            val groups = wrapper.getGroupsOfUser(aUser.id)

            assertThat(groups.size).isGreaterThanOrEqualTo(1)
        }
    }
}