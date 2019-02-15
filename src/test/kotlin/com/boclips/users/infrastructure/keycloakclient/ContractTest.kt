package com.boclips.users.infrastructure.keycloakclient

import com.boclips.users.domain.model.users.IdentityProvider
import com.boclips.users.domain.model.users.KeycloakId
import com.boclips.users.domain.model.users.User
import com.boclips.users.testsupport.UserFactory
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class ContractTest {

    abstract val keycloakClient: IdentityProvider

    lateinit var createdUser: User

    @BeforeEach
    fun setUp() {
        createdUser = keycloakClient.createUserIfDoesntExist(
            UserFactory.sample()
        )
    }

    @AfterEach
    fun tearDown() {
        keycloakClient.deleteUserById(createdUser.keycloakId)
    }

    @Test
    fun `getUserById`() {
        val user: User = keycloakClient.getUserById(createdUser.keycloakId)

        Assertions.assertThat(user.keycloakId.value).isNotEmpty()
        Assertions.assertThat(user.email).isEqualTo(createdUser.email)
        Assertions.assertThat(user.firstName).isEqualTo(createdUser.firstName)
        Assertions.assertThat(user.lastName).isEqualTo(createdUser.lastName)
        Assertions.assertThat(user.email).isEqualTo(createdUser.email)
    }

    @Test
    fun `get invalid user`() {
        assertThrows<ResourceNotFoundException> { keycloakClient.getUserById(KeycloakId(value = "invalidId")) }
    }

    @Test
    fun `new user has not logged in before`() {
        val loggedIn: Boolean = keycloakClient.hasLoggedIn(createdUser.keycloakId)
        Assertions.assertThat(loggedIn).isFalse()
    }

    @Test
    fun `can create and delete user`() {
        val email = "contract-test-user-2@boclips.com"

        val createdUser = keycloakClient.createUserIfDoesntExist(
            UserFactory.sample(
                email = email
            )
        )
        Assertions.assertThat(createdUser.email).isEqualTo(email)
        Assertions.assertThat(createdUser.keycloakId.value).isNotEmpty()

        val deletedUser = keycloakClient.deleteUserById(createdUser.keycloakId!!)
        Assertions.assertThat(deletedUser.email).isEqualTo(email)
    }

//    @Test
//    fun `get list of all users registered since forever`() {
//        val randomEmails =
//            listOf(generateRandomEmail(), generateRandomEmail(), generateRandomEmail(), generateRandomEmail())
//        val usersRegisteredSince =
//            keycloakClient.getUsersRegisteredSince(LocalDateTime.of(LocalDate.ofYearDay(1000, 1), LocalTime.MIN))
//
//        assertThat(usersRegisteredSince.map { it.email }).containsAll(randomEmails)
//    }

    @Test
    fun `can get a list of all users`() {
        val randomEmails =
            listOf(generateRandomEmail(), generateRandomEmail(), generateRandomEmail(), generateRandomEmail())

        randomEmails.forEach { email ->
            keycloakClient.createUserIfDoesntExist(UserFactory.sample(email = email))
        }

        val users = keycloakClient.getUsers()

        assertThat(users.size).isGreaterThanOrEqualTo(4)
        assertThat(users.map { it.email }).containsAll(randomEmails)
    }

    private fun generateRandomEmail() = "user@${UUID.randomUUID()}.com"
}