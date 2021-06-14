package com.boclips.users.infrastructure.keycloak.client

import com.boclips.users.domain.model.user.UserId
import com.boclips.users.domain.model.user.UserSessions
import com.boclips.users.infrastructure.keycloak.KeycloakCreateUserRequest
import com.boclips.users.infrastructure.keycloak.KeycloakWrapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.keycloak.representations.idm.EventRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.mockito.Mockito
import java.time.Instant
import java.util.Date

internal class KeycloakClientTest {
    lateinit var keycloakClient: KeycloakClient
    lateinit var keycloakWrapperMock: KeycloakWrapper

    @BeforeEach
    fun setUp() {
        keycloakWrapperMock = Mockito.mock(KeycloakWrapper::class.java)

        keycloakClient = KeycloakClient(
            keycloakWrapperMock,
            KeycloakUserToAccountConverter()
        )
    }

    @Nested
    inner class GetUserById {
        @Test
        fun `can fetch a valid user`() {
            whenever(keycloakWrapperMock.getUserById(any())).thenReturn(
                UserRepresentation().apply {
                    this.id = "x"
                    this.firstName = "Odete"
                    this.username = "abc@def.xyz"
                    this.lastName = "Portugal"
                    this.isEmailVerified = true
                    this.realmRoles = emptyList()
                    this.createdTimestamp = Instant.now().toEpochMilli()
                }
            )

            val id = UserId(value = "x")
            val account = keycloakClient.getIdentitiesById(id)!!

            assertThat(account.id).isEqualTo(id)
        }

        @Test
        fun `returns null when cannot find a given user`() {
            val id = UserId(value = "x")
            val account = keycloakClient.getIdentitiesById(id)

            assertThat(account).isNull()
        }

        @Test
        fun `returns null when user is missing required fields`() {
            whenever(keycloakWrapperMock.getUserById(any())).thenReturn(
                UserRepresentation().apply {
                    this.id = "x"
                }
            )

            val id = UserId(value = "x")
            val account = keycloakClient.getIdentitiesById(id)

            assertThat(account).isNull()
        }
    }

    @Nested
    inner class getUserSessions {
        @Test
        fun `fetch user session`() {
            whenever(keycloakWrapperMock.getLastUserSession(any())).thenReturn(
                listOf(
                    EventRepresentation().apply {
                        this.time = 1558080047000
                        this.type = "LOGIN"
                    }
                )
            )

            val lastUserSession: UserSessions = keycloakClient.getUserSessions(
                UserId(
                    value = "x"
                )
            )

            assertThat(lastUserSession.hasLoggedIn()).isTrue()
            assertThat(lastUserSession.lastAccess).isEqualTo(Instant.ofEpochMilli(1558080047000))
        }

        @Test
        fun `returns null when user has not logged in`() {
            whenever(keycloakWrapperMock.getLastUserSession(any())).thenReturn(emptyList())

            val lastUserSession = keycloakClient.getUserSessions(
                UserId(
                    value = "x"
                )
            )

            assertThat(lastUserSession.lastAccess).isNull()
            assertThat(lastUserSession.hasLoggedIn()).isFalse()
        }
    }

    @Test
    fun `remove user by id`() {
        val user1 = UserRepresentation().apply {
            this.id = "1"
            this.username = "test@gmail.com"
            this.realmRoles = emptyList()
            this.createdTimestamp = Instant.now().toEpochMilli()
        }
        val user2 = UserRepresentation().apply {
            this.id = "2"
            this.username = "test2@gmail.com"
            this.realmRoles = emptyList()
            this.createdTimestamp = Instant.now().toEpochMilli()
        }

        whenever(keycloakWrapperMock.countUsers()).thenReturn(2)
        whenever(keycloakWrapperMock.getAllUserIds()).thenReturn(
            listOf(
                user1.id,
                user2.id
            )
        )
        assertThat(keycloakClient.count()).isEqualTo(2)

        assertThat(keycloakClient.getAllIdentityIds().toList()[0].value).isEqualTo(user1.id)
        assertThat(keycloakClient.getAllIdentityIds().toList()[1].value).isEqualTo(user2.id)

        keycloakClient.deleteIdentity(UserId("1"))

        whenever(keycloakWrapperMock.countUsers()).thenReturn(1)
        whenever(keycloakWrapperMock.getAllUserIds()).thenReturn(
            listOf(
                user2.id
            )
        )
        assertThat(keycloakClient.count()).isEqualTo(1)

        assertThat(keycloakClient.getAllIdentityIds().toList()[0].value).isEqualTo(user2.id)
    }

    @Nested
    inner class CreateIdentity {
        @Test
        fun `can create a user with a temporary password`() {
            val mockUser = UserRepresentation().apply {
                id = "randomId"
                username = "username"
                realmRoles = listOf("CREATE_B2B_USERS")
                createdTimestamp = Date().time
            }

            whenever(keycloakWrapperMock.createUser(any())).thenReturn(mockUser)
            keycloakClient.createIdentity("funkyemail.com", "temporarypassword", "CREATE_B2B_USERS", true)

            argumentCaptor<KeycloakCreateUserRequest>().apply {
                verify(keycloakWrapperMock).createUser(capture())

                assertThat(firstValue.email).isEqualTo("funkyemail.com")
                assertThat(firstValue.password).isEqualTo("temporarypassword")
                assertThat(firstValue.role).isEqualTo("CREATE_B2B_USERS")
                assertThat(firstValue.isPasswordTemporary).isTrue()
            }
        }
    }
}
