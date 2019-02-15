package com.boclips.users.infrastructure.keycloakclient

import com.boclips.users.domain.model.users.IdentityProvider
import com.boclips.users.testsupport.KeycloakUserFactory
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import java.util.UUID

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
        val email = "test@testtest.com"

        val createdUser = keycloakClient.createUserIfDoesntExist(
            KeycloakUserFactory.sample(
                email = "test@testtest.com",
                firstName = "Hello",
                lastName = "There",
                id = null
            )
        )
        Assertions.assertThat(createdUser.email).isEqualTo(email)
        Assertions.assertThat(createdUser.id).isNotEmpty()

        val deletedUser = keycloakClient.deleteUserById(createdUser.id!!)
        Assertions.assertThat(deletedUser.email).isEqualTo(email)
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

    @Test
    fun `can get a list of all users`() {
        val randomEmails =
            listOf(generateRandomEmail(), generateRandomEmail(), generateRandomEmail(), generateRandomEmail())

        randomEmails.forEach { email ->
            keycloakClient.createUserIfDoesntExist(KeycloakUserFactory.sample(email = email))
        }

        val users = keycloakClient.getUsers()

        assertThat(users.size).isGreaterThanOrEqualTo(4)
        assertThat(users.map { it.username }).containsAll(randomEmails)
    }

    private fun generateRandomEmail() = "user@${UUID.randomUUID()}.com"
}