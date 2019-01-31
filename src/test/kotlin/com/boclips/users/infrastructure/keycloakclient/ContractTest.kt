package com.boclips.users.infrastructure.keycloakclient

import com.boclips.users.domain.model.users.IdentityProvider
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import java.time.LocalDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class ContractTest {
    abstract val keycloakClient: IdentityProvider
    lateinit var createdUser: KeycloakUser

    @BeforeEach
    fun setUp() {
        createdUser = keycloakClient.createUserIfDoesntExist(KeycloakUser(
                username = "yolo",
                id = null,
                email = "some-createdUser@boclips.com",
                firstName = "Hans",
                lastName = "Muster",
                isEmailVerified = true,
                createdAccountAt = LocalDateTime.of(2018, 1, 1, 0, 0))
        )
    }

    @AfterEach
    fun tearDown() {
        keycloakClient.deleteUserById(createdUser.id!!)
    }

    @Test
    fun `getUserById`() {
        val user: KeycloakUser = keycloakClient.getUserById(createdUser.id!!)

        assertThat(user.id).isNotEmpty()
        assertThat(user.username).isEqualTo(createdUser.username)
        assertThat(user.firstName).isEqualTo(createdUser.firstName)
        assertThat(user.lastName).isEqualTo(createdUser.lastName)
        assertThat(user.email).isEqualTo(createdUser.email)
    }

    @Test
    fun `get invalid user`() {
        assertThrows<ResourceNotFoundException> { keycloakClient.getUserById("invalidId") }
    }

    @Test
    fun `new user has not logged in before`() {
        val loggedIn: Boolean = keycloakClient.hasLoggedIn(createdUser.id!!)
        assertThat(loggedIn).isFalse()
    }

    @Test
    fun `can create and delete user`() {
        val username = "contract-test-user-2"

        val createdUser = keycloakClient.createUserIfDoesntExist(KeycloakUser(
                username = username,
                id = null,
                email = "test@testtest.com",
                firstName = "Hello",
                lastName = "There",
                isEmailVerified = true,
                createdAccountAt = LocalDateTime.of(2018, 1, 1, 0, 0)
        ))
        assertThat(createdUser.username).isEqualTo(username)
        assertThat(createdUser.id).isNotEmpty()

        val deletedUser = keycloakClient.deleteUserById(createdUser.id!!)
        assertThat(deletedUser.username).isEqualTo(username)
    }

//    @Test
//    fun `can retrieve new teacher group membership`() {
//        val createdGroup = keycloakClient.createGroupIfDoesntExist(KeycloakGroup(
//                name = "teachers"
//        ))
//
//        keycloakClient.addUserToGroup(createdUser.id!!, createdGroup.id!!)
//
//        val userIds = keycloakClient.getLastAdditionsToTeacherGroup(LocalDate.now().minusDays(100))
//
//        assertThat(userIds).contains(createdUser.id)
//    }

    @Test
    fun `get all users`() {
        val allUsers = keycloakClient.getAllUsers()

        assertThat(allUsers.size).isGreaterThan(0)
    }
}