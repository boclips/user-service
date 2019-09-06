package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.OrganisationFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MongoOrganisationAccountRepositoryTest : AbstractSpringIntegrationTest() {

    @Test
    fun `persists an organisation`() {
        val organisationName = "Persist Organisation"

        val contractIds = listOf(ContractId("Contract A"), ContractId("Contract B"))

        val organisationAccount = organisationAccountRepository.save(
            contractIds = contractIds,
            apiIntegration = OrganisationFactory.apiIntegration(name = organisationName)
        )

        assertThat(organisationAccount.id).isNotNull
        assertThat(organisationAccount.organisation.name).isEqualTo(organisationName)
        assertThat(organisationAccount.contractIds).isEqualTo(contractIds)
    }

    @Test
    fun `looks up an organisation by associated role`() {
        val role = "ROLE_VIEWSONIC"
        val organisation = organisationAccountRepository.save(
            role = role,
            apiIntegration = OrganisationFactory.apiIntegration()
        )

        val foundOrganisation = organisationAccountRepository.findApiIntegrationByRole(role)
        assertThat(organisation).isEqualTo(foundOrganisation)
    }

    @Test
    fun `looks up an organisation by id`() {
        val organisation =
            organisationAccountRepository.save(apiIntegration = OrganisationFactory.apiIntegration())

        val foundOrganisation = organisationAccountRepository.findOrganisationAccountById(organisation.id)

        assertThat(organisation).isEqualTo(foundOrganisation)
    }

    @Test
    fun `looks up schools by name and country`() {
        val correctSchool = organisationAccountRepository.save(
            OrganisationFactory.school(name = "Some School", countryName = "GBR")
        )
        organisationAccountRepository.save(
            apiIntegration = OrganisationFactory.apiIntegration(name = "Some School")
        )
        organisationAccountRepository.save(
            OrganisationFactory.school(name = "Some School", countryName = "POL")
        )
        organisationAccountRepository.save(
            OrganisationFactory.school(name = "Another one", countryName = "GBR")
        )

        val schools = organisationAccountRepository.lookupSchools(
            organisationName = "school",
            country = "GBR"
        )

        assertThat(schools).hasSize(1)
        assertThat(schools.first().id).isEqualTo(correctSchool.id.value)
    }

    @Test
    fun `looks up an api integration by name`() {
        val organisation = organisationAccountRepository.save(
            apiIntegration = OrganisationFactory.apiIntegration(name = "api-name")
        )

        val retrievedOrganisation = organisationAccountRepository.findApiIntegrationByName(name = "api-name")

        assertThat(organisation).isEqualTo(retrievedOrganisation)
    }
}
