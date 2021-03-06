package com.boclips.users.domain.service.marketing

import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.users.testsupport.factories.UserFactory
import com.boclips.users.testsupport.factories.UserSessionsFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant

class UserToCrmProfileTest {

    @Test
    fun `converts a user to a CRM profile`() {
        val lastAccess = Instant.now()
        val sessions = UserSessionsFactory.sample(lastAccess = lastAccess)
        val user = UserFactory.sample()

        val crmProfile = convertUserToCrmProfile(
            user,
            sessions
        )!!

        assertThat(crmProfile.id).isEqualTo(user.id)
        assertThat(crmProfile.activated).isEqualTo(true)
        assertThat(crmProfile.ageRange).isEqualTo(user.profile!!.ages)
        assertThat(crmProfile.subjects).isEqualTo(user.profile!!.subjects)
        assertThat(crmProfile.email).isEqualTo(user.identity.email)
        assertThat(crmProfile.firstName).isEqualTo(user.profile!!.firstName)
        assertThat(crmProfile.lastName).isEqualTo(user.profile!!.lastName)
        assertThat(crmProfile.hasOptedIntoMarketing).isEqualTo(user.profile!!.hasOptedIntoMarketing)

        assertThat(crmProfile.lastLoggedIn).isEqualTo(lastAccess)
    }

    @Test
    fun `marks the user as activated when it has an organisation`() {
        val lastAccess = Instant.now()
        val sessions = UserSessionsFactory.sample(lastAccess = lastAccess)
        val user = UserFactory.sample(organisation = OrganisationFactory.school())

        val crmProfile = convertUserToCrmProfile(
            user,
            sessions
        )!!

        assertThat(crmProfile.id).isEqualTo(user.id)
        assertThat(crmProfile.activated).isEqualTo(true)
    }

    @Test
    fun `returns null when no user profile`() {
        val crmProfile = convertUserToCrmProfile(
            UserFactory.sample(profile = null), UserSessionsFactory.sample()
        )

        assertThat(crmProfile).isNull()
    }

    @Test
    fun `includes some session for users that have not logged in`() {
        val user = UserFactory.sample()
        val sessions = UserSessionsFactory.sample(lastAccess = null)

        val crmProfile = convertUserToCrmProfile(
            user,
            sessions
        )!!

        assertThat(crmProfile.lastLoggedIn).isNull()
    }
}
