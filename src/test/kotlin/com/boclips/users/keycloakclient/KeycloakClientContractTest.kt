package com.boclips.users.keycloakclient

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.yaml.snakeyaml.Yaml
import java.io.InputStream
import java.util.stream.Stream
import javax.ws.rs.NotFoundException

class KeycloakClientContractTest : ContractTest {

    lateinit var createdUser: KeycloakUser

    private val keycloakClient = KeycloakClient(KeycloakConfig(
            url = "https://login.testing-boclips.com/auth",
            username = readSecret("KEYCLOAK_USERNAME"),
            password = readSecret("KEYCLOAK_PASSWORD")
    ))

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
    override fun `getUserById`() {
        val user: KeycloakUser = keycloakClient.getUserById(createdUser.id!!)

        assertThat(user.id).isNotEmpty()
        assertThat(user.username).isEqualTo(createdUser.username)
        assertThat(user.firstName).isEqualTo(createdUser.firstName)
        assertThat(user.lastName).isEqualTo(createdUser.lastName)
        assertThat(user.email).isEqualTo(createdUser.email)
    }

    @Test
    override fun `get invalid user`() {
        assertThrows<NotFoundException> { keycloakClient.getUserById("invalidId") }
    }

    @Test
    override fun `new user has not logged in before`() {
        val loggedIn: Boolean = keycloakClient.hasLoggedIn(createdUser.id!!)
        assertThat(loggedIn).isFalse()
    }

    @Test
    override fun `can create and delete user`() {
        val username = "contract-test-user-2"

        val createdUser = keycloakClient.createUser(KeycloakUser(
                username = username,
                email = "test@testtest.com",
                firstName = "Hello",
                lastName = "There",
                id = null

        ))
        assertThat(createdUser.username).isEqualTo(username)
        assertThat(createdUser.id).isNotEmpty()

        val deletedUser = keycloakClient.deleteUserById(createdUser.id!!)
        assertThat(deletedUser.username).isEqualTo(username)
    }
}

private fun readSecret(key: String): String {
    if (System.getenv(key) != null) {
        return System.getenv(key)
    }

    val yaml = Yaml()
    val inputStream: InputStream =
            KeycloakClientContractTest::javaClass.javaClass.classLoader
                    .getResourceAsStream("contract-test-setup.yml")

    val apiKey = yaml.load<Map<String, String>>(inputStream)[key]!!
    inputStream.close()

    return apiKey
}
