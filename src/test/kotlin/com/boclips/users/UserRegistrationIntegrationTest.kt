package com.boclips.users

import com.boclips.users.application.UserRegistrator
import com.boclips.users.config.SchedulerProperties
import com.boclips.users.domain.model.events.Event
import com.boclips.users.domain.model.events.EventType
import com.boclips.users.domain.service.UserService
import com.boclips.users.infrastructure.mixpanel.MixpanelClientFake
import com.boclips.users.testsupport.AbstractSpringIntergrationTest
import com.boclips.users.testsupport.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class UserRegistrationIntegrationTest : AbstractSpringIntergrationTest() {

    @Autowired
    lateinit var userRegistrator: UserRegistrator

    @Autowired
    lateinit var mixpanelClientFake: MixpanelClientFake

    @Autowired
    lateinit var schedulerProperties: SchedulerProperties

    @Autowired
    lateinit var userService: UserService

    @BeforeEach
    fun setUp() {
        mixpanelClientFake.clear()
    }

    @Test
    fun `user registration eventually triggers`() {
        assertThat(mixpanelClientFake.getEvents()).isEmpty()

        val user = keycloakClientFake.createUserIfDoesntExist(UserFactory.sample(activated = false))
        keycloakClientFake.createRegisteredEvents(user.keycloakId)

        Awaitility.await().untilAsserted {
            assertThat(mixpanelClientFake.getEvents()).containsExactly(
                Event(
                    EventType.ACCOUNT_CREATED,
                    user.keycloakId.value
                )
            )
        }
    }

    @Test
    fun `user registration triggers only once for events when polling`() {
        assertThat(mixpanelClientFake.getEvents()).isEmpty()

        val user = keycloakClientFake.createUserIfDoesntExist(UserFactory.sample(activated = false))
        keycloakClientFake.createRegisteredEvents(user.keycloakId)

        repeat(3) {
            Thread.sleep(schedulerProperties.registrationPeriodInMillis.toLong())
        }

        assertThat(mixpanelClientFake.getEvents()).containsOnlyOnce(
            Event(
                EventType.ACCOUNT_CREATED,
                userId = user.keycloakId.value!!
            )
        )
    }

    @Test
    fun `user registration triggers multiple times if not between timed polls`() {
        assertThat(mixpanelClientFake.getEvents()).isEmpty()

        val user = keycloakClientFake.createUserIfDoesntExist(UserFactory.sample(activated = false))
        keycloakClientFake.createRegisteredEvents(user.keycloakId)

        val timesToRepeat = 3
        repeat(timesToRepeat) {
            userRegistrator.registerNewTeachersSinceLastPoll()
        }

        assertThat(mixpanelClientFake.getEvents().filter { it.userId == user.keycloakId.value }.size)
            .isGreaterThanOrEqualTo(timesToRepeat)
    }

    @Test
    fun `user registration does not modify existing users`() {
        val user = UserFactory.sample(activated = true)
        keycloakClientFake.createUserWithId(user)

        userRegistrator.registerNewTeachersSinceLastPoll()

        assertThat(userService.findById(user.keycloakId.value)).isEqualTo(user)
    }

    @Test
    fun `user registration does not trigger events for existing users`() {
        val user = UserFactory.sample(activated = true)

        keycloakClientFake.createUserIfDoesntExist(user)

        userRegistrator.registerNewTeachersSinceLastPoll()

        assertThat(mixpanelClientFake.getEvents()).hasSize(0)
    }
}
