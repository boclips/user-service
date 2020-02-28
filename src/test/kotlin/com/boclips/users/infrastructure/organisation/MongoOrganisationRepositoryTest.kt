package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.contentpackage.AccessRuleId
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.organisation.DealType
import com.boclips.users.domain.model.organisation.ApiIntegration
import com.boclips.users.domain.model.organisation.OrganisationType
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.service.OrganisationExpiresOnUpdate
import com.boclips.users.domain.service.OrganisationTypeUpdate
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.OrganisationDetailsFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Page
import java.time.ZonedDateTime

class MongoOrganisationRepositoryTest : AbstractSpringIntegrationTest() {

    @Test
    fun `persists an organisation`() {
        val organisationName = "Persist Organisation"

        val accessRulesIds = listOf(AccessRuleId("Contract A"), AccessRuleId("Contract B"))

        val organisation: Organisation<ApiIntegration> = organisationRepository.save(
            apiIntegration = OrganisationDetailsFactory.apiIntegration(
                name = organisationName,
                allowsOverridingUserIds = true
            ),
            accessRuleIds = accessRulesIds
        )

        assertThat(organisation.id).isNotNull
        assertThat(organisation.type).isEqualTo(DealType.STANDARD)
        assertThat(organisation.organisation.name).isEqualTo(organisationName)
        assertThat(organisation.accessRuleIds).isEqualTo(accessRulesIds)
        assertThat((organisation).organisation.allowsOverridingUserIds).isTrue()
    }

    @Test
    fun `persists a school with an existing district`() {
        val district = organisationRepository.save(
            OrganisationDetailsFactory.district(name = "good stuff")
        )
        val school = organisationRepository.save(
            OrganisationDetailsFactory.school(district = district, postCode = "12345")
        )
        val fetchedSchool = organisationRepository.findSchoolById(school.id)

        assertThat(fetchedSchool?.id).isNotNull
        assertThat(fetchedSchool?.type).isEqualTo(DealType.STANDARD)
        assertThat(fetchedSchool?.organisation?.postcode).isEqualTo("12345")
        assertThat(fetchedSchool?.organisation?.district?.organisation?.name).isEqualTo("good stuff")
        assertThat(fetchedSchool?.organisation?.district?.type).isEqualTo(DealType.STANDARD)
        assertThat(fetchedSchool?.organisation?.district?.organisation?.name).isEqualTo("good stuff")
    }

    @Test
    fun `persists a school with an expiry date`() {
        val accessExpiresOn = ZonedDateTime.now().plusDays(1)
        val school = organisationRepository.save(
            school = OrganisationDetailsFactory.school(postCode = "12345"), accessExpiresOn = accessExpiresOn
        )
        val fetchedSchool = organisationRepository.findSchoolById(school.id)

        assertThat(fetchedSchool?.id).isNotNull
        assertThat(fetchedSchool?.type).isEqualTo(DealType.STANDARD)
        assertThat(fetchedSchool?.organisation?.postcode).isEqualTo("12345")
        assertThat(fetchedSchool?.accessExpiresOn).isEqualTo(accessExpiresOn)
    }

    @Test
    fun `persists the expiry date from the parent organisation`() {
        val accessExpiresOn = ZonedDateTime.now().plusDays(1)
        val district = organisationRepository.save(
            district = OrganisationDetailsFactory.district(name = "good stuff"), accessExpiresOn = accessExpiresOn
        )
        val school = organisationRepository.save(
            school = OrganisationDetailsFactory.school(district = district, postCode = "12345")
        )
        val fetchedSchoolAccount = organisationRepository.findSchoolById(school.id)

        assertThat(fetchedSchoolAccount?.id).isNotNull
        assertThat(fetchedSchoolAccount?.type).isEqualTo(DealType.STANDARD)
        assertThat(fetchedSchoolAccount?.organisation?.postcode).isEqualTo("12345")
        assertThat(fetchedSchoolAccount?.organisation?.district?.accessExpiresOn).isEqualTo(accessExpiresOn)
    }

    @Test
    fun `looks up an organisation by associated role`() {
        val role = "ROLE_VIEWSONIC"
        val organisation = organisationRepository.save(
            apiIntegration = OrganisationDetailsFactory.apiIntegration(),
            role = role
        )

        val foundOrganisation = organisationRepository.findApiIntegrationByRole(role)
        assertThat(organisation).isEqualTo(foundOrganisation)
    }

    @Test
    fun `looks up an organisation by id`() {
        val organisation =
            organisationRepository.save(apiIntegration = OrganisationDetailsFactory.apiIntegration())

        val foundOrganisation = organisationRepository.findOrganisationById(organisation.id)

        assertThat(organisation).isEqualTo(foundOrganisation)
    }

    @Test
    fun `looks up schools by name and country`() {
        val correctSchool = organisationRepository.save(
            OrganisationDetailsFactory.school(name = "Some School", countryName = "GBR")
        )
        organisationRepository.save(
            apiIntegration = OrganisationDetailsFactory.apiIntegration(name = "Some School")
        )
        organisationRepository.save(
            OrganisationDetailsFactory.school(name = "Some School", countryName = "POL")
        )
        organisationRepository.save(
            OrganisationDetailsFactory.school(name = "Another one", countryName = "GBR")
        )

        val schools = organisationRepository.lookupSchools(
            schoolName = "school",
            countryCode = "GBR"
        )

        assertThat(schools).hasSize(1)
        assertThat(schools.first().id).isEqualTo(correctSchool.id.value)
    }

    @Test
    fun `looks up an api integration by name`() {
        val organisation = organisationRepository.save(
            apiIntegration = OrganisationDetailsFactory.apiIntegration(name = "api-name")
        )

        val retrievedOrganisation = organisationRepository.findApiIntegrationByName(name = "api-name")

        assertThat(organisation).isEqualTo(retrievedOrganisation)
    }

    @Test
    fun `find school by external id`() {
        val school = organisationRepository.save(
            school = OrganisationDetailsFactory.school(externalId = "external-id")
        )

        val retrievedOrganisation = organisationRepository.findOrganisationByExternalId("external-id")

        assertThat(school).isEqualTo(retrievedOrganisation)
    }

    @Test
    fun `find schools`() {
        val school = organisationRepository.save(OrganisationDetailsFactory.school())
        organisationRepository.save(OrganisationDetailsFactory.district())
        organisationRepository.save(OrganisationDetailsFactory.apiIntegration())

        val allSchools = organisationRepository.findSchools()

        assertThat(allSchools).containsExactlyInAnyOrder(school)
    }

    @Test
    fun `account type update`() {
        val organisation = organisationRepository.save(OrganisationDetailsFactory.district())

        assertThat(organisation.type).isEqualTo(DealType.STANDARD)

        val updatedOrganisation = organisationRepository.update(
            OrganisationTypeUpdate(
                id = organisation.id,
                type = DealType.DESIGN_PARTNER
            )
        )

        assertThat(updatedOrganisation).isNotNull
        assertThat(updatedOrganisation?.type).isEqualTo(DealType.DESIGN_PARTNER)
        assertThat(organisationRepository.findOrganisationById(organisation.id)?.type).isEqualTo(
            DealType.DESIGN_PARTNER
        )
    }

    @Test
    fun `account access expiry update`() {
        val oldExpiry = ZonedDateTime.now()
        val newExpiry = ZonedDateTime.now().plusWeeks(2)
        val organisation = organisationRepository.save(OrganisationDetailsFactory.school(), accessExpiresOn = oldExpiry)

        val updatedOrganisation = organisationRepository.update(
            OrganisationExpiresOnUpdate(
                id = organisation.id,
                accessExpiresOn = newExpiry
            )
        )

        assertThat(updatedOrganisation?.accessExpiresOn).isEqualTo(newExpiry)
    }

    @Test
    fun `update returns null when organisation not found`() {
        val updatedOrganisation = organisationRepository.update(
            OrganisationTypeUpdate(
                id = OrganisationId("doesnotexist"),
                type = DealType.DESIGN_PARTNER
            )
        )
        assertThat(updatedOrganisation).isNull()
    }

    @Test
    fun `find organisations by parent id`() {
        val district = organisationRepository.save(OrganisationDetailsFactory.district())
        organisationRepository.save(OrganisationDetailsFactory.school(district = district))
        organisationRepository.save(OrganisationDetailsFactory.school(district = null))
        organisationRepository.save(OrganisationDetailsFactory.school(district = null))

        assertThat(organisationRepository.findSchools()).hasSize(3)
        assertThat(organisationRepository.findOrganisationsByParentId(district.id)).hasSize(1)
    }

    @Test
    fun `find independent schools and districts by country code`() {
        val district = organisationRepository.save(OrganisationDetailsFactory.district())
        val school = organisationRepository.save(
            OrganisationDetailsFactory.school(
                district = null,
                country = Country.fromCode(Country.USA_ISO)
            )
        )

        organisationRepository.save(
            OrganisationDetailsFactory.school(
                district = district,
                country = Country.fromCode(Country.USA_ISO)
            )
        )

        organisationRepository.save(
            OrganisationDetailsFactory.school(
                district = null,
                country = Country.fromCode("GBR")
            )
        )

        organisationRepository.save(
            OrganisationDetailsFactory.apiIntegration(
                country = Country.fromCode(Country.USA_ISO)
            )
        )

        val independentOrganisations =
            organisationRepository.findOrganisations(
                countryCode = Country.USA_ISO,
                page = 0,
                size = 10,
                types = listOf(OrganisationType.SCHOOL, OrganisationType.DISTRICT)
            )
        assertThat(independentOrganisations).containsExactly(district, school)
        assertThat(independentOrganisations).hasSize(2)

        assertThat(
            organisationRepository.findOrganisations(
                countryCode = "GBR",
                page = 0,
                size = 10,
                types = listOf(OrganisationType.SCHOOL, OrganisationType.DISTRICT)
            )
        ).hasSize(1)
    }

    @Test
    fun `ordering independent organisations by expiry date, then name`() {
        val schoolOne = organisationRepository.save(
            OrganisationDetailsFactory.school(
                name = "schoolA",
                district = null,
                country = Country.fromCode(Country.USA_ISO)
            ),
            ZonedDateTime.now().plusDays(60)
        )

        val schoolTwo = organisationRepository.save(
            OrganisationDetailsFactory.school(
                name = "schoolB",
                district = null,
                country = Country.fromCode(Country.USA_ISO)
            ),
            ZonedDateTime.now().plusDays(5)
        )

        val schoolThree = organisationRepository.save(
            OrganisationDetailsFactory.school(
                name = "schoolC",
                district = null,
                country = Country.fromCode(Country.USA_ISO)
            ),
            ZonedDateTime.now().plusDays(10)
        )

        val schoolSix = organisationRepository.save(
            OrganisationDetailsFactory.school(
                name = "schoolF",
                district = null,
                country = Country.fromCode(Country.USA_ISO)
            )
        )
        val schoolFour = organisationRepository.save(
            OrganisationDetailsFactory.school(
                name = "schoolE",
                district = null,
                country = Country.fromCode(Country.USA_ISO)
            )
        )

        val schoolFive = organisationRepository.save(
            OrganisationDetailsFactory.school(
                name = "schoolD",
                district = null,
                country = Country.fromCode(Country.USA_ISO)
            )
        )

        val independentOrganisations: Page<Organisation<*>>? =
            organisationRepository.findOrganisations(
                countryCode = Country.USA_ISO,
                page = 0,
                size = 6,
                types = listOf(OrganisationType.SCHOOL, OrganisationType.DISTRICT)
            )
        assertThat(independentOrganisations).containsExactly(
            schoolOne,
            schoolThree,
            schoolTwo,
            schoolFive,
            schoolFour,
            schoolSix
        )
    }

    @Test
    fun `it paginates and orders independent organisations by expiry date, then name`() {
        val schoolOne = organisationRepository.save(
            OrganisationDetailsFactory.school(
                name = "schoolA",
                district = null,
                country = Country.fromCode(Country.USA_ISO)
            ),
            ZonedDateTime.now().plusDays(60)
        )

        organisationRepository.save(
            OrganisationDetailsFactory.school(
                name = "schoolB",
                district = null,
                country = Country.fromCode(Country.USA_ISO)
            ),
            ZonedDateTime.now().plusDays(5)
        )

        val schoolThree = organisationRepository.save(
            OrganisationDetailsFactory.school(
                name = "schoolC",
                district = null,
                country = Country.fromCode(Country.USA_ISO)
            ),
            ZonedDateTime.now().plusDays(10)
        )

        organisationRepository.save(
            OrganisationDetailsFactory.school(
                name = "schoolF",
                district = null,
                country = Country.fromCode(Country.USA_ISO)
            )
        )
        organisationRepository.save(
            OrganisationDetailsFactory.school(
                name = "schoolE",
                district = null,
                country = Country.fromCode(Country.USA_ISO)
            )
        )

        organisationRepository.save(
            OrganisationDetailsFactory.school(
                name = "schoolD",
                district = null,
                country = Country.fromCode(Country.USA_ISO)
            )
        )

        val independentOrganisations: Page<Organisation<*>>? =
            organisationRepository.findOrganisations(
                countryCode = Country.USA_ISO,
                page = 0,
                size = 2,
                types = listOf(OrganisationType.SCHOOL, OrganisationType.DISTRICT)
            )

        assertThat(independentOrganisations!!.content).containsExactly(schoolOne, schoolThree)
        assertThat(independentOrganisations.size).isEqualTo(2)
        assertThat(independentOrganisations.totalPages).isEqualTo(3)
        assertThat(independentOrganisations.totalElements).isEqualTo(6)
    }
}
