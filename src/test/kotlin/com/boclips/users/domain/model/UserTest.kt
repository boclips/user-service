package com.boclips.users.domain.model

import com.boclips.users.domain.model.organisation.OrganisationAccountId
import com.boclips.users.testsupport.factories.AccountFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

class UserTest {
    @Test
    fun `the user is lifetime when createdAt is null`() {
        val user = UserFactory.sample(
            account = AccountFactory.sample(
                createdAt = null
            )
        )

        assertThat(user.hasLifetimeAccess()).isTrue()
    }

    @Test
    fun `the user is lifetime when createdAt is before 2019-12-05`() {
        val user = UserFactory.sample(
            account = AccountFactory.sample(
                createdAt = ZonedDateTime.parse("2019-06-06T00:00:00Z")
            )
        )

        assertThat(user.hasLifetimeAccess()).isTrue()
    }

    @Test
    fun `the user is not lifetime when createdAt is after 19-12-05`() {
        val user = UserFactory.sample(
            account = AccountFactory.sample(
                createdAt = ZonedDateTime.parse("2019-12-12T00:00:00Z")
            )
        )

        assertThat(user.hasLifetimeAccess()).isFalse()
    }

    @Test
    fun `the user is not lifetime when createdAt is on 19-12-05`() {
        val user = UserFactory.sample(
            account = AccountFactory.sample(
                createdAt = ZonedDateTime.parse("2019-12-05T00:00:01Z")
            )
        )

        assertThat(user.hasLifetimeAccess()).isFalse()
    }

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
