package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.OrganisationType
import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class MongoOrganisationRepositoryTest : AbstractSpringIntegrationTest() {
    @Test
    fun `persists an organisation`() {
        val organisationName = "Persist Organisation"

        val contractIds = listOf(ContractId("Contract A"), ContractId("Contract B"))
        val organisation = organisationRepository.save(
            organisationName,
            contractIds = contractIds,
            organisationType = OrganisationType.ApiCustomer
        )

        assertThat(organisation.id).isNotNull
        assertThat(organisation.name).isEqualTo(organisationName)
        assertThat(organisation.contractIds).isEqualTo(contractIds)
    }

    @Test
    fun `looks up an organisation by associated role`() {
        val role = "ROLE_VIEWSONIC"
        val organisation = organisationRepository.save(
            organisationName = "blah",
            role = role,
            organisationType = OrganisationType.ApiCustomer
        )

        val foundOrganisation = organisationRepository.findByRole(role)
        assertThat(organisation).isEqualTo(foundOrganisation)
    }

    @Test
    fun `looks up an organisation by id`() {
        val organisation =
            organisationRepository.save(organisationName = "blah", organisationType = OrganisationType.ApiCustomer)

        val foundOrganisation = organisationRepository.findById(organisation.id)

        assertThat(organisation).isEqualTo(foundOrganisation)
    }

    @Test
    fun `looks up an organisation by a district id`() {
        val organisation =
            organisationRepository.save(
                organisationName = "my-school-district",
                districtId = "external-id",
                organisationType = OrganisationType.District
            )

        val retrievedOrganisation = organisationRepository.findByDistrictId(districtId = "external-id")

        assertThat(organisation).isEqualTo(retrievedOrganisation)
    }

    @Nested
    inner class FindByType {
        @Test
        fun `looks up organisation by districts`() {
            val savedOrganisation = organisationRepository.save(
                organisationName = "Some District",
                districtId = "abc",
                organisationType = OrganisationType.District
            )
            organisationRepository.save(
                organisationName = "Not a District",
                districtId = null,
                organisationType = OrganisationType.ApiCustomer
            )

            val districts = organisationRepository.findByType(OrganisationType.District)

            assertThat(districts).hasSize(1)
            assertThat(districts.first().id).isEqualTo(savedOrganisation.id)
        }

        @Test
        fun `looks up organisation by api customers`() {
            organisationRepository.save(
                organisationName = "Some District",
                districtId = "abc",
                organisationType = OrganisationType.District
            )
            val savedOrganisation = organisationRepository.save(
                organisationName = "Some API customer",
                organisationType = OrganisationType.ApiCustomer
            )

            val districts = organisationRepository.findByType(OrganisationType.ApiCustomer)

            assertThat(districts).hasSize(1)
            assertThat(districts.first().id).isEqualTo(savedOrganisation.id)
        }
    }
}
