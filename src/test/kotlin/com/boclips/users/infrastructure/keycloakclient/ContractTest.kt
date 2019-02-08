package com.boclips.users.infrastructure.keycloakclient

import com.boclips.users.domain.model.users.IdentityProvider
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.*
import java.time.LocalDate

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class ContractTest {

    abstract val keycloakClient: IdentityProvider

    lateinit var createdUser: KeycloakUser

    @BeforeEach
    fun setUp() {
        createdUser = keycloakClient.createUserIfDoesntExist(
            KeycloakUser(
                email = "some-createdUser@boclips.com",
                firstName = "Hans",
                lastName = "Muster",
                username = "yolo",
                id = null
            )
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
        assertThrows<ResourceNotFoundException> { keycloakClient.getUserById("invalidId") }
    }

    @Test
    fun `new user has not logged in before`() {
        val loggedIn: Boolean = keycloakClient.hasLoggedIn(createdUser.id!!)
        Assertions.assertThat(loggedIn).isFalse()
    }

    @Test
    fun `can create and delete user`() {
        val username = "contract-test-user-2"

        val createdUser = keycloakClient.createUserIfDoesntExist(
            KeycloakUser(
                username = username,
                email = "test@testtest.com",
                firstName = "Hello",
                lastName = "There",
                id = null

            )
        )
        Assertions.assertThat(createdUser.username).isEqualTo(username)
        Assertions.assertThat(createdUser.id).isNotEmpty()

        val deletedUser = keycloakClient.deleteUserById(createdUser.id!!)
        Assertions.assertThat(deletedUser.username).isEqualTo(username)
    }

    @Test
    fun `can retrieve new teacher group membership`() {
        val createdGroup = keycloakClient.createGroupIfDoesntExist(
            KeycloakGroup(
                name = "teachers"
            )
        )

        keycloakClient.addUserToGroup(createdUser.id!!, createdGroup.id!!)
        val userIds = keycloakClient.getLastAdditionsToTeacherGroup(LocalDate.now().minusDays(1))

        Assertions.assertThat(userIds).contains(createdUser.id)
    }
}