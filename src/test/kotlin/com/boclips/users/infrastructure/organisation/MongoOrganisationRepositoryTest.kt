package com.boclips.users.infrastructure.organisation

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MongoOrganisationRepositoryTest : AbstractSpringIntegrationTest() {
    @Test
    fun `persists an organisation`() {
        val organisationName = "Persist Organisation"

        val persisted = organisationRepository.save(organisationName)

        assertThat(persisted.id).isNotNull
        assertThat(persisted.name).isEqualTo(organisationName)
    }

    @Test
    fun `looks up an organisation by associated role`() {
        val role = "ROLE_VIEWSONIC"
        val organisation = organisationRepository.save(organisationName = "blah", role = role)

        val foundOrganisation = organisationRepository.findByRole(role)
        assertThat(organisation).isEqualTo(foundOrganisation)
    }
}
