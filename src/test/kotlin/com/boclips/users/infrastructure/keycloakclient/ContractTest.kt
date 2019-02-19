package com.boclips.users.infrastructure.keycloakclient

import com.boclips.users.domain.model.users.Identity
import com.boclips.users.domain.service.IdentityProvider
import com.boclips.users.infrastructure.keycloakclient.KeycloakClient.Companion.TEACHERS_GROUP_NAME
import com.boclips.users.testsupport.UserIdentityFactory
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.LocalDate
import java.util.UUID

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class ContractTest {

    abstract val keycloakClient: IdentityProvider

    abstract val keycloakTestSupport: LowLevelKeycloakClient

    private lateinit var createdIdentity: Identity

    @BeforeEach
    fun setUp() {
        createdIdentity = keycloakTestSupport.createUser(
            Identity(
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
        keycloakTestSupport.deleteUserById(createdIdentity.id)
    }

    @Test
    fun `getUserById`() {
        val user = keycloakClient.getUserById(createdIdentity.id)!!

        Assertions.assertThat(user.id).isNotEmpty()
        Assertions.assertThat(user.firstName).isEqualTo(createdIdentity.firstName)
        Assertions.assertThat(user.lastName).isEqualTo(createdIdentity.lastName)
        Assertions.assertThat(user.email).isEqualTo(createdIdentity.email)
    }

    @Test
    fun `get invalid user`() {
        assertThat(keycloakClient.getUserById("invalidId")).isNull()
    }

    @Test
    fun `new user has not logged in before`() {
        val loggedIn: Boolean = keycloakClient.hasLoggedIn(createdIdentity.id!!)
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
        keycloakTestSupport.addUserToGroup(createdIdentity.id, createdGroup.id!!)

        val users = keycloakClient.getNewTeachers(LocalDate.now().minusDays(1))

        Assertions.assertThat(users).contains(createdIdentity)
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