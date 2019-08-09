package com.boclips.users.infrastructure.user

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
    fun `looks an organisation up by name`() {
        val organisationName = "Lookup Organisation"
        val organisation = organisationRepository.save(organisationName)

        val foundOrganisation = organisationRepository.findByName(organisationName)

        assertThat(foundOrganisation).isEqualTo(organisation)
    }

    @Test
    fun `returns null if organisation is not found by name`() {
        assertThat(organisationRepository.findByName("This does not exist")).isNull()
    }
}
