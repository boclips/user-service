package com.boclips.users

import com.boclips.users.application.UserRegistrator
import com.boclips.users.domain.model.events.Event
import com.boclips.users.domain.model.events.EventType
import com.boclips.users.domain.model.users.IdentityProvider.Companion.TEACHERS_GROUP_NAME
import com.boclips.users.domain.model.users.User
import com.boclips.users.domain.service.UserService
import com.boclips.users.infrastructure.keycloakclient.KeycloakClientFake
import com.boclips.users.infrastructure.keycloakclient.KeycloakGroup
import com.boclips.users.infrastructure.keycloakclient.KeycloakUser
import com.boclips.users.infrastructure.mixpanel.MixpanelClientFake
import com.boclips.users.testsupport.AbstractSpringIntergrationTest
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class UserRegistrationIntegrationTest : AbstractSpringIntergrationTest() {

    @Autowired
    lateinit var userRegistrator: UserRegistrator

    @Autowired
    lateinit var keycloakClientFake: KeycloakClientFake

    @Autowired
    lateinit var mixpanelClientFake: MixpanelClientFake

    @Autowired
    lateinit var userService: UserService

    @BeforeEach
    internal fun setUp() {
        keycloakClientFake.clear()
        mixpanelClientFake.clear()
    }

    @Test
    fun `user registration eventually triggers`() {
        assertThat(mixpanelClientFake.getEvents()).isEmpty()

        val user = keycloakClientFake.createUserIfDoesntExist(KeycloakUser("username"))
        val group = keycloakClientFake.createGroupIfDoesntExist(KeycloakGroup(name = TEACHERS_GROUP_NAME))
        keycloakClientFake.addUserToGroup(userId = user.id!!, groupId = group.id!!)

        Awaitility.await().untilAsserted {
            assertThat(mixpanelClientFake.getEvents()).containsExactly(Event(EventType.ACCOUNT_CREATED, user.id!!))
        }
    }

    @Test
    fun `user registration triggers only once`() {
        val user = keycloakClientFake.createUserIfDoesntExist(KeycloakUser("username"))
        val group = keycloakClientFake.createGroupIfDoesntExist(KeycloakGroup(name = TEACHERS_GROUP_NAME))
        keycloakClientFake.addUserToGroup(userId = user.id!!, groupId = group.id!!)

        repeat(3) { userRegistrator.registerNewTeachersSinceYesterday() }

        assertThat(mixpanelClientFake.getEvents()).containsOnlyOnce(Event(EventType.ACCOUNT_CREATED, userId = user.id!!))
    }

    @Test
    fun `user registration does not modify existing users`() {
        val user = KeycloakUser("username", id = "id")
        userService.activate("id")

        keycloakClientFake.createUserIfDoesntExist(user)
        keycloakClientFake.login(user)

        userRegistrator.registerNewTeachersSinceYesterday()

        assertThat(userService.findById("id")).isEqualTo(User(id = "id", activated = true))
    }

    @Test
    fun `user registration does not trigger events for existing users`() {
        val user = KeycloakUser("username", id = "id")
        userService.activate("id")

        keycloakClientFake.createUserIfDoesntExist(user)
        keycloakClientFake.login(user)

        userRegistrator.registerNewTeachersSinceYesterday()

        assertThat(mixpanelClientFake.getEvents()).hasSize(0)
    }
}