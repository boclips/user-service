package com.boclips.users

import com.boclips.users.application.UserRegistrator
import com.boclips.users.domain.model.events.Event
import com.boclips.users.domain.model.events.EventType
import com.boclips.users.domain.model.users.User
import com.boclips.users.domain.service.UserService
import com.boclips.users.infrastructure.keycloakclient.KeycloakClientFake
import com.boclips.users.infrastructure.keycloakclient.KeycloakUser
import com.boclips.users.infrastructure.mixpanel.MixpanelClientFake
import com.boclips.users.testsupport.AbstractSpringIntergrationTest
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.Repeat

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
        val user = KeycloakUser("username", id = "id")
        assertThat(mixpanelClientFake.getEvents()).isEmpty()

        keycloakClientFake.createUser(user)
        keycloakClientFake.login(user)

        Awaitility.await().untilAsserted {
            assertThat(mixpanelClientFake.getEvents()).containsExactly(Event(EventType.BEGIN_ACTIVATION, "id"))
        }
    }

    @Test
    fun `user registration triggers only once`() {
        val user = KeycloakUser("username", id = "id")

        keycloakClientFake.createUser(user)
        keycloakClientFake.login(user)

        repeat(3) { userRegistrator.registerNewTeachersSinceYesterday() }

        assertThat(mixpanelClientFake.getEvents()).hasSize(1)
    }

    @Test
    fun `user registration does not modify existing users`() {
        val user = KeycloakUser("username", id = "id")
        userService.activate("id")

        keycloakClientFake.createUser(user)
        keycloakClientFake.login(user)

        userRegistrator.registerNewTeachersSinceYesterday()

        assertThat(userService.findById("id")).isEqualTo(User(id = "id", activated = true))
    }

    @Test
    fun `user registration does not trigger events for existing users`() {
        val user = KeycloakUser("username", id = "id")
        userService.activate("id")

        keycloakClientFake.createUser(user)
        keycloakClientFake.login(user)

        userRegistrator.registerNewTeachersSinceYesterday()

        assertThat(mixpanelClientFake.getEvents()).hasSize(0)
    }
}