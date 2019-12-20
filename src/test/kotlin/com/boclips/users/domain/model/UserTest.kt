package com.boclips.users.domain.model

import com.boclips.users.domain.model.account.AccountId
import com.boclips.users.testsupport.factories.ProfileFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UserTest {
    @Test
    fun `the user has onboarded when it is associated with an organisation`() {
        val user = UserFactory.sample(
            organisationAccountId = AccountId("org-id-123")
        )

        assertThat(user.hasOnboarded()).isTrue()
    }

    @Test
    fun `the user has not onboarded when it is not associated with an organisation`() {
        val user = UserFactory.sample(
            organisationAccountId = null
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
