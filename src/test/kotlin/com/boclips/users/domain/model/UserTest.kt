package com.boclips.users.domain.model

import com.boclips.users.domain.model.organisation.OrganisationAccountId
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UserTest {
    @Test
    fun `the user has onboarded when it is associated with an organisation`() {
        val user = UserFactory.sample(
            organisationAccountId = OrganisationAccountId("org-id-123")
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
}
