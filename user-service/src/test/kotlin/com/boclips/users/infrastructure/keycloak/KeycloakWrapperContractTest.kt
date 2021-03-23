package com.boclips.users.infrastructure.keycloak

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.keycloak.admin.client.Keycloak
import org.keycloak.representations.idm.UserRepresentation
import org.springframework.util.ResourceUtils
import org.yaml.snakeyaml.Yaml
import java.io.InputStream
import java.util.UUID

class KeycloakWrapperContractTest {
    lateinit var wrapper: KeycloakWrapper

    @BeforeEach
    fun setUp() {
        val keycloakInstance: Keycloak = Keycloak.getInstance(
            "https://login.staging-boclips.com/auth",
            KeycloakWrapper.REALM,
            readSecret("KEYCLOAK_USERNAME"),
            readSecret("KEYCLOAK_PASSWORD"),
            "boclips-admin"
        )

        wrapper = KeycloakWrapper(keycloak = keycloakInstance, pageSize = 3)
    }

    @Test
    fun `can create and delete a user`() {
        val randomEmail = generateRandomEmail()

        val createdUser = wrapper.createUser(
            KeycloakCreateUserRequest(
                email = randomEmail,
                password = "12345678",
                role = null
            )
        )

        assertThat(createdUser.id).isNotNull()
        assertThat(createdUser.username).isEqualTo(randomEmail)
        assertThat(createdUser.email).isEqualTo(randomEmail)
        assertThat(createdUser.createdTimestamp).isNotNull()

        wrapper.removeUser(createdUser.id)
    }

    @Test
    fun `new users can be created with a role`() {
        val createdUser = wrapper.createUser(
            KeycloakCreateUserRequest(
                email = generateRandomEmail(),
                password = "12345678",
                role = "ROLE_TEACHER"
            )
        )

        assertThat(createdUser.realmRoles).contains("ROLE_TEACHER")

        wrapper.removeUser(createdUser.id)
    }

    @Test
    fun `throws when user already exists`() {
        val user = KeycloakCreateUserRequest(
            email = generateRandomEmail(),
            password = "12345678",
            role = null
        )

        assertThrows<UserAlreadyExistsException> {
            wrapper.createUser(user)
            wrapper.createUser(user)
        }

        wrapper.removeUser(user.email)
    }

    @Test
    fun `can fetch users`() {
//        val createdUser = wrapper.createUser(
//            KeycloakCreateUserRequest(
//                email = generateRandomEmail(),
//                password = "12345678",
//                role = "ROLE_TEACHER"
//            )
//        )

        val keycloakProdInstance: Keycloak = Keycloak.getInstance(
            "https://login.boclips.com/auth",
            KeycloakWrapper.REALM,
            "",
            "",
            "boclips-admin"
        )

        val prodWrapper = KeycloakWrapper(keycloakProdInstance)
        val users: List<UserRepresentation> = prodWrapper.users().toList()

        assertThat(users.size).isEqualTo(prodWrapper.countUsers())

//        val theChosenUser = users.find { (it.id == createdUser.id) }!!
//        assertThat(theChosenUser.id).isNotNull()
//        assertThat(theChosenUser.firstName).isNull()
//        assertThat(theChosenUser.lastName).isNull()
//        assertThat(theChosenUser.email).isNotNull()
//        assertThat(theChosenUser.realmRoles).contains("ROLE_TEACHER")
    }

    @Test
    @Disabled
    fun `can count users`() {
        val count = wrapper.countUsers()

        assertThat(count).isGreaterThan(1)
    }

    @Test
    @Disabled
    fun `add role to user`() {
        val aUser = wrapper.users().first()

        wrapper.addRealmRoleToUser("ROLE_TEACHER", aUser.id)

        assertThat(wrapper.getUserByUsername(aUser.username)!!.realmRoles.contains("ROLE_TEACHER")).isTrue()
    }

    @Nested
    @DisplayName("Testing sessions and last login")
    inner class Sessions {
        @Test
        @Disabled
        fun `cannot fetch user session for a user that does not exist`() {
            val userSessionRepresentation = wrapper.getLastUserSession("x")

            assertThat(userSessionRepresentation).isEmpty()
        }

        @Test
        @Disabled
        fun `cannot fetch user session for a user that has never logged in`() {
            val createdUser = wrapper.createUser(
                KeycloakCreateUserRequest(
                    email = generateRandomEmail(),
                    password = "12345678",
                    role = null
                )
            )

            val userSessionRepresentation = wrapper.getLastUserSession(createdUser.id)

            assertThat(userSessionRepresentation).isEmpty()

            wrapper.removeUser(createdUser.id)
        }

        @Test
        @Disabled("https://www.pivotaltracker.com/n/projects/2383097/stories/168451916")
        fun `can fetch user session for a user that has logged in`() {
            val aUser = wrapper.getUserByUsername("user-service@boclips.com")
            val lastLoginEvents = wrapper.getLastUserSession(aUser!!.id)

            assertThat(lastLoginEvents).isNotEmpty
            assertThat(lastLoginEvents.first().time).isGreaterThan(1558080047000)
        }
    }

    @Nested
    inner class GetUserTest {
        @Test
        @Disabled
        fun `returns null when user does not exist`() {
            val user: UserRepresentation? = wrapper.getUserById("4567890")

            assertThat(user).isNull()
        }

        @Test
        @Disabled
        fun `returns user`() {
            val createdUser = wrapper.createUser(
                KeycloakCreateUserRequest(
                    email = generateRandomEmail(),
                    password = "12345678",
                    role = "ROLE_TEACHER"
                )
            )

            val retrieved: UserRepresentation = wrapper.getUserById(createdUser.id)!!

            assertThat(retrieved.id).isNotNull()
            assertThat(retrieved.email).isNotNull()
            assertThat(retrieved.realmRoles).contains("ROLE_TEACHER")
        }
    }

    @Nested
    inner class GetUserByUsernameTest {
        @Test
        @Disabled
        fun `gets user by username`() {
            val createdUser = wrapper.createUser(
                KeycloakCreateUserRequest(
                    email = generateRandomEmail(),
                    password = "12345678",
                    role = "ROLE_TEACHER"
                )
            )

            val user: UserRepresentation = wrapper.getUserByUsername(createdUser.username)!!

            assertThat(user.id).isEqualTo(createdUser.id)
            assertThat(user.email).isNotNull()
            assertThat(user.realmRoles).contains("ROLE_TEACHER")
        }

        @Test
        @Disabled
        fun `returns null for non-existent username`() {

            val user: UserRepresentation? = wrapper.getUserByUsername("this should not exist")

            assertThat(user).isNull()
        }
    }

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

    private fun generateRandomEmail(): String {
        val randomUsername = UUID.randomUUID().toString()
        return "ben+$randomUsername@boclips.com"
    }
}
