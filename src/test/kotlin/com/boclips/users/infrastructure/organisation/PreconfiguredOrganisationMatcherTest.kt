package com.boclips.users.infrastructure.organisation

import com.boclips.security.utils.User
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PreconfiguredOrganisationMatcherTest : AbstractSpringIntegrationTest() {
    @Test
    fun `matches a teacher to "Boclips for Teachers"`() {
        val user = User(false, "user-id", setOf("ROLE_TEACHER"))

        val matched = organisationMatcher.match(user)

        assertThat(matched).isNotNull
        assertThat(matched!!.name).isEqualTo("Boclips for Teachers")
    }

    @Test
    fun `matches a viewsonic user to "ViewSonic MyViewBoard"`() {
        val user = User(false, "user-id", setOf("ROLE_VIEWSONIC"))

        val matched = organisationMatcher.match(user)

        assertThat(matched).isNotNull
        assertThat(matched!!.name).isEqualTo("ViewSonic MyViewBoard")
    }

    @Test
    fun `matches a pearson user to "Pearson MyRealize"`() {
        val user = User(false, "user-id", setOf("ROLE_PEARSON_MYREALIZE"))

        val matched = organisationMatcher.match(user)

        assertThat(matched).isNotNull
        assertThat(matched!!.name).isEqualTo("Pearson MyRealize")
    }

    @Test
    fun `returns null if user does not have a preconfigured role`() {
        val user = User(false, "user-id", setOf("ROLE_STUDENT"))

        val matched = organisationMatcher.match(user)

        assertThat(matched).isNull()
    }

    @BeforeEach
    fun insertFixtures() {
        organisationRepository.save("Boclips for Teachers")
        organisationRepository.save("ViewSonic MyViewBoard")
        organisationRepository.save("Pearson MyRealize")
    }
}
