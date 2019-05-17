package com.boclips.users.domain.service

import com.boclips.users.testsupport.UserFactory
import com.boclips.users.testsupport.UserSessionsFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant

class UserToCrmProfileTest {

    @Test
    fun `converts a user to a CRM profile`() {
        val lastAccess = Instant.now()
        val sessions = UserSessionsFactory.sample(lastAccess = lastAccess)
        val user = UserFactory.sample()

        val crmProfile = userToCrmProfile(user, sessions)

        assertThat(crmProfile.id).isEqualTo(user.id)
        assertThat(crmProfile.activated).isEqualTo(user.activated)
        assertThat(crmProfile.ageRange).isEqualTo(user.ageRange)
        assertThat(crmProfile.subjects).isEqualTo(user.subjects)
        assertThat(crmProfile.email).isEqualTo(user.email)
        assertThat(crmProfile.firstName).isEqualTo(user.firstName)
        assertThat(crmProfile.lastName).isEqualTo(user.lastName)
        assertThat(crmProfile.hasOptedIntoMarketing).isEqualTo(user.hasOptedIntoMarketing)

        assertThat(crmProfile.lastLoggedIn).isEqualTo(lastAccess)
    }

    @Test
    fun `includes some session for users that have not logged in`() {
        val user = UserFactory.sample()
        val sessions = UserSessionsFactory.sample(lastAccess = null)
        val crmProfile = userToCrmProfile(user, sessions)

        assertThat(crmProfile.lastLoggedIn).isNull()
    }
}