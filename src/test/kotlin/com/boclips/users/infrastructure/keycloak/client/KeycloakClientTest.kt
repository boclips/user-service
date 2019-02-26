package com.boclips.users.infrastructure.keycloak.client

import com.boclips.users.domain.model.identity.IdentityId
import com.boclips.users.infrastructure.keycloak.KeycloakWrapper
import com.boclips.users.testsupport.UserIdentityFactory
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.keycloak.representations.idm.EventRepresentation
import org.keycloak.representations.idm.GroupRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.mockito.Mockito
import java.time.LocalDate

internal class KeycloakClientTest {
    lateinit var keycloakClient: KeycloakClient
    lateinit var keycloakWrapperMock: KeycloakWrapper

    @BeforeEach
    fun setUp() {
        keycloakWrapperMock = Mockito.mock(KeycloakWrapper::class.java)
        keycloakClient = KeycloakClient(
            keycloakWrapperMock,
            KeycloakUserToUserIdentityConverter()
        )
    }

    @Nested
    inner class getUserById {
        @Test
        fun `can fetch a valid user`() {
            whenever(keycloakWrapperMock.getUser(any())).thenReturn(UserRepresentation().apply {
                this.id = "x"
                this.firstName = "Odete"
                this.email = "abc@def.xyz"
                this.lastName = "Portugal"
                this.isEmailVerified = true
            })

            val id = IdentityId(value = "x")
            val identity = keycloakClient.getUserById(id)!!

            assertThat(identity.id).isEqualTo(id)
        }

        @Test
        fun `returns null when cannot find a given user`() {
            val id = IdentityId(value = "x")
            val identity = keycloakClient.getUserById(id)

            assertThat(identity).isNull()
        }

        @Test
        fun `returns null when user is missing required fields`() {
            whenever(keycloakWrapperMock.getUser(any())).thenReturn(UserRepresentation().apply {
                this.id = "x"
            })

            val id = IdentityId(value = "x")
            val identity = keycloakClient.getUserById(id)

            assertThat(identity).isNull()
        }
    }

    @Test
    fun getNewTeachers() {
        whenever(keycloakWrapperMock.getRegisterEvents(any())).thenReturn(
            listOf(
                EventRepresentation().apply {
                    type = "REGISTER"
                    userId = "new teacher"
                },
                EventRepresentation().apply {
                    type = "REGISTER"
                    userId = "not new teacher"
                }
            )
        )

        whenever(keycloakWrapperMock.getGroupsOfUser("new teacher")).thenReturn(
            listOf(
                GroupRepresentation().apply { this.name = KeycloakClient.TEACHERS_GROUP_NAME }
            )
        )

        whenever(keycloakWrapperMock.getUser(any())).thenReturn(
            UserRepresentation().apply {
                this.firstName = "New"
                this.lastName = "Teacher"
                this.email = "newTeacher@gmail.com"
                this.id = "new teacher"
                this.isEmailVerified = false
            }
        )

        val newTeachers = keycloakClient.getNewTeachers(since = LocalDate.now().minusDays(1))

        assertThat(newTeachers).containsExactly(
            UserIdentityFactory.sample(
                id = "new teacher",
                firstName = "New",
                lastName = "Teacher",
                email = "newTeacher@gmail.com",
                isVerified = false
            )
        )
    }

    @Test
    fun getUsers() {
        val user1 = UserRepresentation().apply {
            this.id = "1"
            this.isEmailVerified = true
            this.email = "test@gmail.com"
            this.firstName = "test"
            this.lastName = "test"

        }
        val user2 = UserRepresentation().apply {
            this.id = "2"
            this.isEmailVerified = false
            this.email = "test2@gmail.com"
            this.firstName = "test2"
            this.lastName = "test2"
        }

        whenever(keycloakWrapperMock.countUsers()).thenReturn(2)
        whenever(keycloakWrapperMock.users()).thenReturn(
            listOf(
                user1,
                user2
            )
        )

        val users = keycloakClient.getUsers()

        assertThat(users).hasSize(2)
        assertThat(users).containsExactly(
            UserIdentityFactory.sample(
                id = user1.id,
                isVerified = user1.isEmailVerified,
                email = user1.email,
                firstName = user1.firstName,
                lastName = user1.lastName
            ),
            UserIdentityFactory.sample(
                id = user2.id,
                isVerified = user2.isEmailVerified,
                email = user2.email,
                firstName = user2.firstName,
                lastName = user2.lastName
            )
        )
    }
}