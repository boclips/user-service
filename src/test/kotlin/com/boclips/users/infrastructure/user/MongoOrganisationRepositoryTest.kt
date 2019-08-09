package com.boclips.users.infrastructure.user

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.OrganisationFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MongoOrganisationRepositoryTest : AbstractSpringIntegrationTest() {
    @Test
    fun `persists an organisation`() {
        val organisation = OrganisationFactory.sample()

        val persisted = organisationRepository.save(organisation)

        assertThat(persisted).isEqualTo(organisation)
    }

    @Test
    fun `looks an organisation up by name`() {
        val organisationName = "Lookup Organisation"
        val organisation = organisationRepository.save(OrganisationFactory.sample(
            name = organisationName
        ))

        val foundOrganisation = organisationRepository.findByName(organisationName)

        assertThat(foundOrganisation).isEqualTo(organisation)
    }

    @Test
    fun `returns null if organisation is not found by name`() {
        assertThat(organisationRepository.findByName("This does not exist")).isNull()
    }
}
