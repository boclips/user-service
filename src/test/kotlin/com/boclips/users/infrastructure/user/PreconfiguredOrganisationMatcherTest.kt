package com.boclips.users.infrastructure.user

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
        assertThat(matched!!.name).isEqualTo(organisationName)
    }

    @Test
    fun `returns null if user does not have a preconfigured role`() {
        val user = User(false, "user-id", setOf("ROLE_STUDENT"))

        val matched = organisationMatcher.match(user)

        assertThat(matched).isNull()
    }

    val organisationName = "Boclips for Teachers"

    @BeforeEach
    fun insertFixtures() {
        if (organisationRepository.findByName(organisationName) == null) {
            organisationRepository.save(organisationName)
        }
    }
}
