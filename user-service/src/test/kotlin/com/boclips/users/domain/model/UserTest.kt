package com.boclips.users.domain.model

import com.boclips.users.testsupport.factories.ProfileFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UserTest {
    @Test
    fun `the user has onboarded when they have a firstname`() {
        val user = UserFactory.sample(
            profile = ProfileFactory.sample(firstName = "Rebecca")
        )

        assertThat(user.hasOnboarded()).isTrue()
    }

    @Test
    fun `the user has not onboarded when they do not have a first name`() {
        val user = UserFactory.sample(
            profile = ProfileFactory.sample(firstName = "")
        )

        assertThat(user.hasOnboarded()).isFalse()
    }

    @Test
    fun `the user has been activated when they have given us their name`() {
        val user = UserFactory.sample(
            profile = ProfileFactory.sample(firstName = "Dummy")
        )

        assertThat(user.isActivated()).isTrue()
    }

    @Test
    fun `the user is not considered activated without profile information`() {
        val user = UserFactory.sample(
            profile = ProfileFactory.sample(firstName = "")
        )

        assertThat(user.isActivated()).isFalse()
    }
}
