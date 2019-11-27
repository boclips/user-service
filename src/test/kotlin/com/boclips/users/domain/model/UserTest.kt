package com.boclips.users.domain.model

import com.boclips.users.testsupport.factories.UserFactory
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import java.time.ZonedDateTime

class UserTest {
    @Test
    fun `A user without an expiry date should have access` () {
        val user = UserFactory.sample(accessExpiry = null)
        assertThat(user.hasAccess()).isEqualTo(true)
    }
    @Test
    fun `A user with a future expiry date should have access` () {
        val user = UserFactory.sample(accessExpiry = ZonedDateTime.now().plusDays(1))
        assertThat(user.hasAccess()).isEqualTo(true)
    }
    @Test
    fun `A user with a past expiry date should not have access` () {
        val user = UserFactory.sample(accessExpiry = ZonedDateTime.now().minusDays(1))
        assertThat(user.hasAccess()).isEqualTo(false)
    }
}
