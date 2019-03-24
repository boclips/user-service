package com.boclips.users.application

import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.analytics.Event
import com.boclips.users.domain.model.analytics.EventType
import com.boclips.users.domain.service.UserService
import com.boclips.users.infrastructure.mixpanel.MixpanelClientFake
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.KeycloakClientFake
import com.boclips.users.testsupport.UserIdentityFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.Ignore
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@Disabled
class RegisterUserLegacyIntegrationTest : AbstractSpringIntegrationTest() {

    @Autowired
    lateinit var registerUserLegacy: RegisterUserLegacy

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
    fun `ACCOUNT_CREATED event only gets fired once for each new user`() {
        val user = keycloakClientFake.createUser(UserIdentityFactory.sample())
        val metadata = metadataProvider.getMetadata(user.id)

        repeat(3) { registerUserLegacy.registerNewTeachersSinceYesterday() }

        assertThat(mixpanelClientFake.getEvents()).containsOnlyOnce(
            Event(
                EventType.ACCOUNT_CREATED,
                userId = metadata.analyticsId!!.value
            )
        )
    }

    @Test
    fun `do not re-activate already activated users`() {
        val identity = UserIdentityFactory.sample(email = "username@gmail.com", id = "id")
        userService.activate(UserId(value = "id"))

        keycloakClientFake.createUser(identity)

        registerUserLegacy.registerNewTeachersSinceYesterday()

        val retrievedUser = userService.findById(UserId(value = "id"))
        assertThat(retrievedUser.account.id).isEqualTo(UserId(value = "id"))
        assertThat(retrievedUser.account.activated).isTrue()
    }

    @Test
    fun `ACCOUNT_CREATED event does get fired events for activated users`() {
        val user = UserIdentityFactory.sample(email = "username@gmail.com", id = "id")
        userService.activate(UserId(value = "id"))

        keycloakClientFake.createUser(user)

        registerUserLegacy.registerNewTeachersSinceYesterday()

        assertThat(mixpanelClientFake.getEvents()).hasSize(0)
    }
}