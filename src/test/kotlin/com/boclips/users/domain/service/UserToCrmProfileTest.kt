package com.boclips.users.domain.service

import com.boclips.users.testsupport.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UserToCrmProfileTest {

    @Test
    fun `converts a user to a CRM profile`() {
        val user = UserFactory.sample()

        val crmProfile = userToCrmProfile(user)

        assertThat(crmProfile.id).isEqualTo(user.id)
        assertThat(crmProfile.activated).isEqualTo(user.activated)
        assertThat(crmProfile.ageRange).isEqualTo(user.ageRange)
        assertThat(crmProfile.subjects).isEqualTo(user.subjects)
        assertThat(crmProfile.email).isEqualTo(user.email)
        assertThat(crmProfile.firstName).isEqualTo(user.firstName)
        assertThat(crmProfile.lastName).isEqualTo(user.lastName)
        assertThat(crmProfile.hasOptedIntoMarketing).isEqualTo(user.hasOptedIntoMarketing)
    }
}