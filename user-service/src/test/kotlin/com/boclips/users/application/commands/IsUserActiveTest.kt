package com.boclips.users.application.commands

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.UserFactory
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.ZonedDateTime

class IsUserActiveTest : AbstractSpringIntegrationTest() {

    @Autowired
    lateinit var isUserActive: IsUserActive

    @Test
    fun `active user`() {
        val inTenMinutes = ZonedDateTime.now().plusMinutes(10);
        val user = saveUser(UserFactory.sample(accessExpiresOn = inTenMinutes))
        assertTrue(isUserActive(user.id.value))
    }

    @Test
    fun `inactive user`() {
        val tenMinutesAgo = ZonedDateTime.now().minusMinutes(10);
        val user = saveUser(UserFactory.sample(accessExpiresOn = tenMinutesAgo))
        assertFalse(isUserActive(user.id.value))
    }

    @Test
    fun `expiry date set to null`() {
        val user = saveUser(UserFactory.sample(accessExpiresOn = null))
        assertFalse(isUserActive(user.id.value))
    }

    @Test
    fun `inactive user when couldn't be found`() {
        assertFalse(isUserActive("some-id"))
    }
}
