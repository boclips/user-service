package com.boclips.users.infrastructure.organisation

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PreconfiguredOrganisationMatcherTest : AbstractSpringIntegrationTest() {
    @Test
    fun `matches a teacher to "Boclips for Teachers"`() {
        val matched = organisationMatcher.match(listOf("ROLE_TEACHER"))

        assertThat(matched).isNotNull
        assertThat(matched!!.name).isEqualTo("Boclips for Teachers")
    }

    @Test
    fun `matches a viewsonic user to "ViewSonic MyViewBoard"`() {
        val matched = organisationMatcher.match(listOf("ROLE_VIEWSONIC"))

        assertThat(matched).isNotNull
        assertThat(matched!!.name).isEqualTo("ViewSonic MyViewBoard")
    }

    @Test
    fun `matches a pearson user to "Pearson MyRealize"`() {
        val matched = organisationMatcher.match(listOf("ROLE_PEARSON_MYREALIZE"))

        assertThat(matched).isNotNull
        assertThat(matched!!.name).isEqualTo("Pearson MyRealize")
    }

    @Test
    fun `returns null if user does not have a preconfigured role`() {
        val matched = organisationMatcher.match(listOf("ROLE_STUDENT"))

        assertThat(matched).isNull()
    }

    @BeforeEach
    fun insertFixtures() {
        organisationRepository.save("Boclips for Teachers")
        organisationRepository.save("ViewSonic MyViewBoard")
        organisationRepository.save("Pearson MyRealize")
    }
}
