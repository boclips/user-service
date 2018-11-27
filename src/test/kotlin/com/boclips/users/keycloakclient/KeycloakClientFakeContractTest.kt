package com.boclips.users.keycloakclient

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import javax.ws.rs.NotFoundException

class KeycloakClientFakeContractTest : ContractTest {
    private lateinit var keycloakClient: IdentityProvider

    private val createdUser = KeycloakUser(
            username = "garbage",
            email = "garbage@burning.io",
            firstName = "Hans",
            lastName = "Muster",
            id = "123"
    )

    @BeforeEach
    fun setUp() {
        keycloakClient = KeycloakClientFake()

        keycloakClient.createUser(createdUser)
    }

    @AfterEach
    fun tearDown() {
        keycloakClient.deleteUserById("123")
    }

    @Test
    override fun getUserById() {
        val user = keycloakClient.getUserById("123")

        assertThat(user.id).isNotEmpty()
        assertThat(user.username).isEqualTo(createdUser.username)
        assertThat(user.firstName).isEqualTo(createdUser.firstName)
        assertThat(user.lastName).isEqualTo(createdUser.lastName)
        assertThat(user.email).isEqualTo(createdUser.email)
    }

    @Test
    override fun `get invalid user`() {
        assertThrows<NotFoundException> { keycloakClient.getUserById("invalid-user") }
    }

    @Test
    override fun `new user has not logged in before`() {
        val loggedIn: Boolean = keycloakClient.hasLoggedIn(createdUser.id!!)
        assertThat(loggedIn).isFalse()

    }

    @Test
    override fun `can create and delete user`() {
        val username = "contract-test-createdUser-2"

        val createdUser = keycloakClient.createUser(KeycloakUser(
                username = username,
                email = "test@testtest.com",
                firstName = "Hello",
                lastName = "There",
                id = "newUserId"

        ))
        assertThat(createdUser.username).isEqualTo(username)
        assertThat(createdUser.id).isNotEmpty()

        val deletedUser = keycloakClient.deleteUserById(createdUser.id!!)
        assertThat(deletedUser.username).isEqualTo("contract-test-createdUser-2")
    }
}