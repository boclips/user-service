package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.domain.model.organisation.OrganisationAccountId
import com.boclips.users.domain.model.organisation.OrganisationAccountType
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.service.OrganisationAccountExpiresOnUpdate
import com.boclips.users.domain.service.OrganisationAccountTypeUpdate
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.OrganisationFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

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
        assertThat(organisationAccount.type).isEqualTo(OrganisationAccountType.STANDARD)
        assertThat(organisationAccount.organisation.name).isEqualTo(organisationName)
        assertThat(organisationAccount.contractIds).isEqualTo(contractIds)
    }

    @Test
    fun `persists a school with an existing district`() {
        val district = organisationAccountRepository.save(
            OrganisationFactory.district(name = "good stuff")
        )
        val school = organisationAccountRepository.save(
            OrganisationFactory.school(district = district, postCode = "12345")
        )
        val fetchedSchool = organisationAccountRepository.findSchoolById(school.id)

        assertThat(fetchedSchool?.id).isNotNull
        assertThat(fetchedSchool?.type).isEqualTo(OrganisationAccountType.STANDARD)
        assertThat(fetchedSchool?.organisation?.postcode).isEqualTo("12345")
        assertThat(fetchedSchool?.organisation?.district?.organisation?.name).isEqualTo("good stuff")
        assertThat(fetchedSchool?.organisation?.district?.type).isEqualTo(OrganisationAccountType.STANDARD)
        assertThat(fetchedSchool?.organisation?.district?.organisation?.name).isEqualTo("good stuff")
    }

    @Test
    fun `persists a school with an expiry date`() {
        val accessExpiresOn = ZonedDateTime.now().plusDays(1)
        val school = organisationAccountRepository.save(
            school = OrganisationFactory.school(postCode = "12345"), accessExpiresOn = accessExpiresOn
        )
        val fetchedSchool = organisationAccountRepository.findSchoolById(school.id)

        assertThat(fetchedSchool?.id).isNotNull
        assertThat(fetchedSchool?.type).isEqualTo(OrganisationAccountType.STANDARD)
        assertThat(fetchedSchool?.organisation?.postcode).isEqualTo("12345")
        assertThat(fetchedSchool?.accessExpiresOn).isEqualTo(accessExpiresOn)
    }

    @Test
    fun `persists the expiry date from the parent organisation`() {
        val accessExpiresOn = ZonedDateTime.now().plusDays(1)
        val district = organisationAccountRepository.save(
            district = OrganisationFactory.district(name = "good stuff"), accessExpiresOn = accessExpiresOn
        )
        val school = organisationAccountRepository.save(
            school = OrganisationFactory.school(district = district, postCode = "12345")
        )
        val fetchedSchoolAccount = organisationAccountRepository.findSchoolById(school.id)

        assertThat(fetchedSchoolAccount?.id).isNotNull
        assertThat(fetchedSchoolAccount?.type).isEqualTo(OrganisationAccountType.STANDARD)
        assertThat(fetchedSchoolAccount?.organisation?.postcode).isEqualTo("12345")
        assertThat(fetchedSchoolAccount?.organisation?.district?.accessExpiresOn).isEqualTo(accessExpiresOn)
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

    @Test
    fun `account type update`() {
        val organisation = organisationAccountRepository.save(OrganisationFactory.district())

        assertThat(organisation.type).isEqualTo(OrganisationAccountType.STANDARD)

        val updatedOrganisation = organisationAccountRepository.update(
            OrganisationAccountTypeUpdate(
                id = organisation.id,
                type = OrganisationAccountType.DESIGN_PARTNER
            )
        )

        assertThat(updatedOrganisation).isNotNull
        assertThat(updatedOrganisation?.type).isEqualTo(OrganisationAccountType.DESIGN_PARTNER)
        assertThat(organisationAccountRepository.findOrganisationAccountById(organisation.id)?.type).isEqualTo(
            OrganisationAccountType.DESIGN_PARTNER
        )
    }

    @Test
    fun `account access expiry update`() {
        val oldExpiry = ZonedDateTime.now()
        val newExpiry = ZonedDateTime.now().plusWeeks(2)
        val organisation = organisationAccountRepository.save(OrganisationFactory.school(), accessExpiresOn = oldExpiry)

        val updatedOrganisation = organisationAccountRepository.update(
            OrganisationAccountExpiresOnUpdate(
                id = organisation.id,
                accessExpiresOn = newExpiry
            )
        )

        assertThat(updatedOrganisation?.accessExpiresOn).isEqualTo(newExpiry)
    }

    @Test
    fun `update returns null when organisation not found`() {
        val updatedOrganisation = organisationAccountRepository.update(
            OrganisationAccountTypeUpdate(
                id = OrganisationAccountId("doesnotexist"),
                type = OrganisationAccountType.DESIGN_PARTNER
            )
        )
        assertThat(updatedOrganisation).isNull()
    }

    @Test
    fun `find organisations by parent id`() {
        val district = organisationAccountRepository.save(OrganisationFactory.district())
        organisationAccountRepository.save(OrganisationFactory.school(district = district))
        organisationAccountRepository.save(OrganisationFactory.school(district = null))
        organisationAccountRepository.save(OrganisationFactory.school(district = null))

        assertThat(organisationAccountRepository.findSchools()).hasSize(3)
        assertThat(organisationAccountRepository.findOrganisationAccountsByParentId(district.id)).hasSize(1)
    }

    @Test
    fun `find independent schools and districts by country code`() {
        val district = organisationAccountRepository.save(OrganisationFactory.district())
        val school = organisationAccountRepository.save(
            OrganisationFactory.school(
                district = null,
                country = Country.fromCode(Country.USA_ISO)
            )
        )

        organisationAccountRepository.save(
            OrganisationFactory.school(
                district = district,
                country = Country.fromCode(Country.USA_ISO)
            )
        )

        organisationAccountRepository.save(
            OrganisationFactory.school(
                district = null,
                country = Country.fromCode("GBR")
            )
        )

        organisationAccountRepository.save(
            OrganisationFactory.apiIntegration(
                country = Country.fromCode(Country.USA_ISO)
            )
        )

        val independentOrganisations =
            organisationAccountRepository.findIndependentSchoolsAndDistricts(Country.USA_ISO)
        assertThat(independentOrganisations!![0]).isEqualTo(district)
        assertThat(independentOrganisations!![1]).isEqualTo(school)
        assertThat(independentOrganisations).hasSize(2)

        assertThat(organisationAccountRepository.findIndependentSchoolsAndDistricts("GBR")).hasSize(1)
    }
}
