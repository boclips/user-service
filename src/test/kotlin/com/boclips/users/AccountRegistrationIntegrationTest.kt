package com.boclips.users

import com.boclips.users.application.UserRegistrator
import com.boclips.users.domain.model.account.Account
import com.boclips.users.domain.model.analytics.Event
import com.boclips.users.domain.model.analytics.EventType
import com.boclips.users.domain.model.identity.IdentityId
import com.boclips.users.domain.service.UserService
import com.boclips.users.infrastructure.keycloakclient.KeycloakClient.Companion.TEACHERS_GROUP_NAME
import com.boclips.users.infrastructure.keycloakclient.KeycloakClientFake
import com.boclips.users.infrastructure.keycloakclient.KeycloakGroup
import com.boclips.users.infrastructure.mixpanel.MixpanelClientFake
import com.boclips.users.testsupport.AbstractSpringIntergrationTest
import com.boclips.users.testsupport.UserIdentityFactory
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class AccountRegistrationIntegrationTest : AbstractSpringIntergrationTest() {

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

        val user =
            keycloakClientFake.createUser(UserIdentityFactory.sample(email = "username@gmail.com"))
        val group = keycloakClientFake.createGroup(KeycloakGroup(name = TEACHERS_GROUP_NAME))

        keycloakClientFake.addUserToGroup(userId = user.id.value, groupId = group.id!!)

        Awaitility.await().untilAsserted {
            assertThat(mixpanelClientFake.getEvents()).containsExactly(
                Event(
                    EventType.ACCOUNT_CREATED,
                    user.id.value
                )
            )
        }
    }

    @Test
    fun `user registration triggers only once`() {
        val user =
            keycloakClientFake.createUser(UserIdentityFactory.sample(email = "username@gmail.com"))
        val group = keycloakClientFake.createGroup(KeycloakGroup(name = TEACHERS_GROUP_NAME))
        keycloakClientFake.addUserToGroup(userId = user.id.value, groupId = group.id!!)

        repeat(3) { userRegistrator.registerNewTeachersSinceYesterday() }

        assertThat(mixpanelClientFake.getEvents()).containsOnlyOnce(
            Event(
                EventType.ACCOUNT_CREATED,
                userId = user.id.value
            )
        )
    }

    @Test
    fun `user registration does not modify existing users`() {
        val identity = UserIdentityFactory.sample(email = "username@gmail.com", id = "id")
        userService.activate("id")

        keycloakClientFake.createUser(identity)
        keycloakClientFake.login(identity)

        userRegistrator.registerNewTeachersSinceYesterday()

        val retrievedUser = userService.findById(IdentityId(value = "id"))
        assertThat(retrievedUser.account).isEqualTo(
            Account(
                id = "id",
                activated = true
            )
        )
    }

    @Test
    fun `user registration does not trigger events for existing users`() {
        val user = UserIdentityFactory.sample(email = "username@gmail.com", id = "id")
        keycloakClientFake.createUser(user)
        userService.activate("id")

        keycloakClientFake.createUser(user)
        keycloakClientFake.login(user)

        userRegistrator.registerNewTeachersSinceYesterday()

        assertThat(mixpanelClientFake.getEvents()).hasSize(0)
    }
}