package com.boclips.users.infrastructure.keycloakclient

import com.boclips.users.domain.model.users.IdentityProvider
import com.boclips.users.domain.model.users.IdentityProvider.Companion.TEACHERS_GROUP_NAME
import com.boclips.users.domain.model.users.UserIdentity
import com.boclips.users.testsupport.UserIdentityFactory
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

    abstract val keycloakTestSupport: LowLevelKeycloakClient

    private lateinit var createdUser: UserIdentity

    @BeforeEach
    fun setUp() {
        createdUser = keycloakTestSupport.createUser(
            UserIdentity(
                email = "some-createdUser@boclips.com",
                firstName = "Hans",
                lastName = "Muster",
                id = "",
                isVerified = false
            )
        )
    }

    @AfterEach
    fun tearDown() {
        keycloakTestSupport.deleteUserById(createdUser.id!!)
    }

    @Test
    fun `getUserById`() {
        val user = keycloakClient.getUserById(createdUser.id!!)

        Assertions.assertThat(user.id).isNotEmpty()
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

        val createdUser = keycloakTestSupport.createUser(
            UserIdentityFactory.sample(
                email = "test@testtest.com",
                firstName = "Hello",
                lastName = "There"
            )
        )
        Assertions.assertThat(createdUser.email).isEqualTo(email)
        Assertions.assertThat(createdUser.id).isNotEmpty()

        val deletedUser = keycloakTestSupport.deleteUserById(createdUser.id)
        Assertions.assertThat(deletedUser.email).isEqualTo(email)
    }

    @Test
    fun `can retrieve new teachers`() {
        val createdGroup = keycloakTestSupport.createGroup(KeycloakGroup(name = TEACHERS_GROUP_NAME))
        keycloakTestSupport.addUserToGroup(createdUser.id, createdGroup.id!!)

        val userIds = keycloakClient.getNewTeachers(LocalDate.now().minusDays(1))

        Assertions.assertThat(userIds).contains(createdUser.id)
    }

    @Test
    fun `can get a list of all users`() {
        val randomEmails =
            listOf(generateRandomEmail(), generateRandomEmail(), generateRandomEmail(), generateRandomEmail())

        randomEmails.forEach { email ->
            keycloakTestSupport.createUser(UserIdentityFactory.sample(email = email))
        }

        val users = keycloakClient.getUsers()

        assertThat(users.size).isGreaterThanOrEqualTo(4)
        assertThat(users.map { it.email }).containsAll(randomEmails)
    }

    private fun generateRandomEmail() = "user@${UUID.randomUUID()}.com"
}