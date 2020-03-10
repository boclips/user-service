package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.contentpackage.AccessRuleId
import com.boclips.users.domain.model.contentpackage.ContentPackageId
import com.boclips.users.domain.model.organisation.ApiIntegration
import com.boclips.users.domain.model.organisation.DealType
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.organisation.OrganisationType
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.service.OrganisationExpiresOnUpdate
import com.boclips.users.domain.service.OrganisationTypeUpdate
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.OrganisationDetailsFactory
import com.boclips.users.testsupport.factories.OrganisationFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Page
import java.time.ZonedDateTime

class MongoOrganisationRepositoryTest : AbstractSpringIntegrationTest() {

    @Test
    fun `persists an organisation`() {
        val organisationName = "Persist Organisation"


        val organisation: Organisation<ApiIntegration> = OrganisationFactory.sample(
            type = DealType.STANDARD,
            contentPackageId = ContentPackageId(value = "123"),
            details = OrganisationDetailsFactory.apiIntegration(
                name = organisationName,
                allowsOverridingUserIds = true
            )
        )

        val retrievedOrganisation = organisationRepository.save(organisation)

        assertThat(retrievedOrganisation.id).isNotNull
        assertThat(retrievedOrganisation.type).isEqualTo(DealType.STANDARD)
        assertThat(retrievedOrganisation.details.name).isEqualTo(organisationName)
        assertThat(retrievedOrganisation.details.allowsOverridingUserIds).isTrue()
        assertThat(retrievedOrganisation.contentPackageId?.value).isEqualTo("123")
    }

    @Test
    fun `persists a school with an existing district`() {
        val district = organisationRepository.save(
            OrganisationFactory.sample(details = OrganisationDetailsFactory.district(name = "good stuff"))
        )
        val school = organisationRepository.save(
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.school(
                    district = district,
                    postCode = "12345"
                )
            )
        )
        val fetchedSchool = organisationRepository.findSchoolById(school.id)

        assertThat(fetchedSchool?.id).isNotNull
        assertThat(fetchedSchool?.type).isEqualTo(DealType.STANDARD)
        assertThat(fetchedSchool?.details?.postcode).isEqualTo("12345")
        assertThat(fetchedSchool?.details?.district?.details?.name).isEqualTo("good stuff")
        assertThat(fetchedSchool?.details?.district?.type).isEqualTo(DealType.STANDARD)
        assertThat(fetchedSchool?.details?.district?.details?.name).isEqualTo("good stuff")
    }

    @Test
    fun `persists a school with an expiry date`() {
        val accessExpiresOn = ZonedDateTime.now().plusDays(1)
        val school = organisationRepository.save(
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.school(postCode = "12345"),
                accessExpiresOn = accessExpiresOn
            )
        )
        val fetchedSchool = organisationRepository.findSchoolById(school.id)

        assertThat(fetchedSchool?.id).isNotNull
        assertThat(fetchedSchool?.type).isEqualTo(DealType.STANDARD)
        assertThat(fetchedSchool?.details?.postcode).isEqualTo("12345")
        assertThat(fetchedSchool?.accessExpiresOn).isEqualTo(accessExpiresOn)
    }

    @Test
    fun `persists the expiry date from the parent organisation`() {
        val accessExpiresOn = ZonedDateTime.now().plusDays(1)
        val district = organisationRepository.save(
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.district(name = "good stuff"),
                accessExpiresOn = accessExpiresOn
            )
        )
        val school = organisationRepository.save(
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.school(district = district, postCode = "12345")
            )
        )

        val fetchedSchoolAccount = organisationRepository.findSchoolById(school.id)

        assertThat(fetchedSchoolAccount?.id).isNotNull
        assertThat(fetchedSchoolAccount?.type).isEqualTo(DealType.STANDARD)
        assertThat(fetchedSchoolAccount?.details?.postcode).isEqualTo("12345")
        assertThat(fetchedSchoolAccount?.details?.district?.accessExpiresOn).isEqualTo(accessExpiresOn)
    }

    @Test
    fun `looks up an organisation by associated role`() {
        val role = "ROLE_VIEWSONIC"
        val organisation = organisationRepository.save(
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.apiIntegration(),
                role = role
            )
        )

        val foundOrganisation = organisationRepository.findApiIntegrationByRole(role)
        assertThat(organisation).isEqualTo(foundOrganisation)
    }

    @Test
    fun `looks up an organisation by id`() {
        val organisation =
            organisationRepository.save(OrganisationFactory.sample(details = OrganisationDetailsFactory.apiIntegration()))

        val foundOrganisation = organisationRepository.findOrganisationById(organisation.id)

        assertThat(organisation).isEqualTo(foundOrganisation)
    }

    @Test
    fun `looks up schools by name and country`() {
        val correctSchool = organisationRepository.save(
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.school(
                    name = "Some School",
                    countryName = "GBR"
                )
            )
        )
        organisationRepository.save(
            OrganisationFactory.sample(details = OrganisationDetailsFactory.apiIntegration(name = "Some School"))
        )
        organisationRepository.save(
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.school(
                    name = "Some School",
                    countryName = "POL"
                )
            )
        )
        organisationRepository.save(
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.school(
                    name = "Another one",
                    countryName = "GBR"
                )
            )
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
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.apiIntegration(name = "api-name")
            )
        )

        val retrievedOrganisation = organisationRepository.findApiIntegrationByName(name = "api-name")

        assertThat(organisation).isEqualTo(retrievedOrganisation)
    }

    @Test
    fun `find school by external id`() {
        val school = organisationRepository.save(
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.school(externalId = "external-id")
            )
        )
        val retrievedOrganisation = organisationRepository.findOrganisationByExternalId("external-id")

        assertThat(school).isEqualTo(retrievedOrganisation)
    }

    @Test
    fun `find schools`() {
        val school =
            organisationRepository.save(OrganisationFactory.sample(details = OrganisationDetailsFactory.school()))
        organisationRepository.save(OrganisationFactory.sample(details = OrganisationDetailsFactory.district()))
        organisationRepository.save(OrganisationFactory.sample(details = OrganisationDetailsFactory.apiIntegration()))

        val allSchools = organisationRepository.findSchools()

        assertThat(allSchools).containsExactlyInAnyOrder(school)
    }

    @Test
    fun `account type update`() {
        val organisation =
            organisationRepository.save(OrganisationFactory.sample(details = OrganisationDetailsFactory.district()))

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
        val organisation = organisationRepository.save(
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.school(),
                accessExpiresOn = oldExpiry
            )
        )

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
        val district =
            organisationRepository.save(OrganisationFactory.sample(details = OrganisationDetailsFactory.district()))
        organisationRepository.save(OrganisationFactory.sample(details = OrganisationDetailsFactory.school(district = district)))
        organisationRepository.save(OrganisationFactory.sample(details = OrganisationDetailsFactory.school(district = null)))
        organisationRepository.save(OrganisationFactory.sample(details = OrganisationDetailsFactory.school(district = null)))

        assertThat(organisationRepository.findSchools()).hasSize(3)
        assertThat(organisationRepository.findOrganisationsByParentId(district.id)).hasSize(1)
    }

    @Test
    fun `find independent schools and districts by country code`() {
        val district =
            organisationRepository.save(OrganisationFactory.sample(details = OrganisationDetailsFactory.district()))
        val school = organisationRepository.save(
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.school(
                    district = null,
                    country = Country.fromCode(Country.USA_ISO)
                )
            )

        )
        organisationRepository.save(
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.school(
                    district = district,
                    country = Country.fromCode(Country.USA_ISO)
                )
            )

        )
        organisationRepository.save(
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.school(
                    district = null,
                    country = Country.fromCode("GBR")
                )
            )

        )
        organisationRepository.save(
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.apiIntegration(
                    country = Country.fromCode(Country.USA_ISO)
                )
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
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.school(
                    name = "schoolA",
                    district = null,
                    country = Country.fromCode(Country.USA_ISO)
                ),
                accessExpiresOn = ZonedDateTime.now().plusDays(60)
            )
        )

        val schoolTwo = organisationRepository.save(
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.school(
                    name = "schoolB",
                    district = null,
                    country = Country.fromCode(Country.USA_ISO)
                ),
                accessExpiresOn = ZonedDateTime.now().plusDays(5)
            )
        )

        val schoolThree = organisationRepository.save(
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.school(
                    name = "schoolC",
                    district = null,
                    country = Country.fromCode(Country.USA_ISO)
                ),
                accessExpiresOn = ZonedDateTime.now().plusDays(10)
            )
        )

        val schoolSix = organisationRepository.save(
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.school(
                    name = "schoolF",
                    district = null,
                    country = Country.fromCode(Country.USA_ISO)
                )
            )
        )
        val schoolFour = organisationRepository.save(
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.school(
                    name = "schoolE",
                    district = null,
                    country = Country.fromCode(Country.USA_ISO)
                )
            )
        )

        val schoolFive = organisationRepository.save(
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.school(
                    name = "schoolD",
                    district = null,
                    country = Country.fromCode(Country.USA_ISO)
                )
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
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.school(
                    name = "schoolA",
                    district = null,
                    country = Country.fromCode(Country.USA_ISO)
                ),
                accessExpiresOn = ZonedDateTime.now().plusDays(60)
            )
        )

        organisationRepository.save(
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.school(
                    name = "schoolB",
                    district = null,
                    country = Country.fromCode(Country.USA_ISO)
                ),
                accessExpiresOn = ZonedDateTime.now().plusDays(5)
            )
        )

        val schoolThree = organisationRepository.save(
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.school(
                    name = "schoolC",
                    district = null,
                    country = Country.fromCode(Country.USA_ISO)
                ),
                accessExpiresOn = ZonedDateTime.now().plusDays(10)
            )
        )

        organisationRepository.save(
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.school(
                    name = "schoolF",
                    district = null,
                    country = Country.fromCode(Country.USA_ISO)
                )
            )
        )
        organisationRepository.save(
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.school(
                    name = "schoolE",
                    district = null,
                    country = Country.fromCode(Country.USA_ISO)
                )
            )
        )

        organisationRepository.save(
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.school(
                    name = "schoolD",
                    district = null,
                    country = Country.fromCode(Country.USA_ISO)
                )
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
