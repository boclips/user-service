package com.boclips.users.keycloakclient

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.*
import javax.ws.rs.NotFoundException

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class ContractTest {

    abstract val keycloakClient: IdentityProvider

    lateinit var createdUser: KeycloakUser

    @BeforeEach
    fun setUp() {
        createdUser = keycloakClient.createUser(KeycloakUser(
                email = "some-createdUser@boclips.com",
                firstName = "Hans",
                lastName = "Muster",
                username = "yolo",
                id = null)
        )
    }

    @AfterEach
    fun tearDown() {
        keycloakClient.deleteUserById(createdUser.id!!)
    }

    @Test
    fun `getUserById`() {
        val user: KeycloakUser = keycloakClient.getUserById(createdUser.id!!)

        Assertions.assertThat(user.id).isNotEmpty()
        Assertions.assertThat(user.username).isEqualTo(createdUser.username)
        Assertions.assertThat(user.firstName).isEqualTo(createdUser.firstName)
        Assertions.assertThat(user.lastName).isEqualTo(createdUser.lastName)
        Assertions.assertThat(user.email).isEqualTo(createdUser.email)
    }

    @Test
    fun `get invalid user`() {
        assertThrows<NotFoundException> { keycloakClient.getUserById("invalidId") }
    }

    @Test
    fun `new user has not logged in before`() {
        val loggedIn: Boolean = keycloakClient.hasLoggedIn(createdUser.id!!)
        Assertions.assertThat(loggedIn).isFalse()
    }

    @Test
    fun `can create and delete user`() {
        val username = "contract-test-user-2"

        val createdUser = keycloakClient.createUser(KeycloakUser(
                username = username,
                email = "test@testtest.com",
                firstName = "Hello",
                lastName = "There",
                id = null

        ))
        Assertions.assertThat(createdUser.username).isEqualTo(username)
        Assertions.assertThat(createdUser.id).isNotEmpty()

        val deletedUser = keycloakClient.deleteUserById(createdUser.id!!)
        Assertions.assertThat(deletedUser.username).isEqualTo(username)
    }
}