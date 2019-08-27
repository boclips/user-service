package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MongoOrganisationRepositoryTest : AbstractSpringIntegrationTest() {
    @Test
    fun `persists an organisation`() {
        val organisationName = "Persist Organisation"

        val contractIds = listOf(ContractId("Contract A"), ContractId("Contract B"))
        val organisation = organisationRepository.save(
            organisationName,
            contractIds = contractIds
        )

        assertThat(organisation.id).isNotNull
        assertThat(organisation.name).isEqualTo(organisationName)
        assertThat(organisation.contractIds).isEqualTo(contractIds)
    }

    @Test
    fun `looks up an organisation by associated role`() {
        val role = "ROLE_VIEWSONIC"
        val organisation = organisationRepository.save(organisationName = "blah", role = role)

        val foundOrganisation = organisationRepository.findByRole(role)
        assertThat(organisation).isEqualTo(foundOrganisation)
    }

    @Test
    fun `looks up an organisation by id`() {
        val organisation = organisationRepository.save(organisationName = "blah")

        val foundOrganisation = organisationRepository.findById(organisation.id)

        assertThat(organisation).isEqualTo(foundOrganisation)
    }
}
