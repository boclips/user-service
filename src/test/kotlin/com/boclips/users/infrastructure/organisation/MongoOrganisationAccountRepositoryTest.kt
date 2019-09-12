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
            apiIntegration = OrganisationFactory.apiIntegration(name = organisationName),
            contractIds = contractIds
        )

        assertThat(organisationAccount.id).isNotNull
        assertThat(organisationAccount.organisation.name).isEqualTo(organisationName)
        assertThat(organisationAccount.contractIds).isEqualTo(contractIds)
    }

    @Test
    fun `persists a school with an existing district`() {
        val district = organisationAccountRepository.save(
            OrganisationFactory.district(name = "good stuff")
        )
        val school = organisationAccountRepository.save(
            OrganisationFactory.school(district = district)
        )

        assertThat(school.id).isNotNull
        assertThat((school.organisation).district?.organisation?.name).isEqualTo("good stuff")
    }

    @Test
    fun `looks up an organisation by associated role`() {
        val role = "ROLE_VIEWSONIC"
        val organisation = organisationAccountRepository.save(
            apiIntegration = OrganisationFactory.apiIntegration(),
            role = role
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
            schoolName = "school",
            countryCode = "GBR"
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

    @Test
    fun `find school by external id`() {
        val school = organisationAccountRepository.save(
            school = OrganisationFactory.school(externalId = "external-id")
        )

        val retrievedOrganisation = organisationAccountRepository.findOrganisationAccountByExternalId("external-id")

        assertThat(school).isEqualTo(retrievedOrganisation)
    }

    @Test
    fun `find schools`() {
        val school = organisationAccountRepository.save(OrganisationFactory.school())
        organisationAccountRepository.save(OrganisationFactory.district())
        organisationAccountRepository.save(OrganisationFactory.apiIntegration())

        val allSchools = organisationAccountRepository.findSchools()

        assertThat(allSchools).containsExactlyInAnyOrder(school)
    }
}
