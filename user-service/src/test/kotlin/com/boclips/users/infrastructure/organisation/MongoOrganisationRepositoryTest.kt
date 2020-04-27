package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.Page
import com.boclips.users.domain.model.contentpackage.ContentPackageId
import com.boclips.users.domain.model.organisation.Address
import com.boclips.users.domain.model.organisation.ApiIntegration
import com.boclips.users.domain.model.organisation.DealType
import com.boclips.users.domain.model.organisation.ExternalOrganisationId
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.organisation.OrganisationType
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.service.OrganisationUpdate.ReplaceDealType
import com.boclips.users.domain.service.OrganisationUpdate.ReplaceDomain
import com.boclips.users.domain.service.OrganisationUpdate.ReplaceExpiryDate
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.users.testsupport.factories.OrganisationFactory.Companion.deal
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

class MongoOrganisationRepositoryTest : AbstractSpringIntegrationTest() {

    @Nested
    inner class PersistOrganisation {
        @Test
        fun `persists an organisation`() {
            val organisationName = "Persist Organisation"

            val organisation: ApiIntegration = OrganisationFactory.apiIntegration(
                name = organisationName,
                deal = deal(
                    type = DealType.STANDARD,
                    contentPackageId = ContentPackageId(value = "123")
                ),
                allowsOverridingUserId = true
            )

            val retrievedOrganisation = organisationRepository.save(organisation)

            assertThat(retrievedOrganisation.id).isNotNull
            assertThat(retrievedOrganisation.deal.type).isEqualTo(DealType.STANDARD)
            assertThat(retrievedOrganisation.name).isEqualTo(organisationName)
            assertThat(retrievedOrganisation.allowsOverridingUserIds).isTrue()
            assertThat(retrievedOrganisation.deal.contentPackageId?.value).isEqualTo("123")
        }

        @Test
        fun `persists a school with an existing district`() {
            val district = organisationRepository.save(
                OrganisationFactory.district(name = "good stuff")
            )
            val school = organisationRepository.save(
                OrganisationFactory.school(
                    district = district,
                    address = Address(
                        postcode = "12345"
                    )
                )
            )
            val fetchedSchool = organisationRepository.findSchoolById(school.id)

            assertThat(fetchedSchool?.id).isNotNull
            assertThat(fetchedSchool?.deal?.type).isEqualTo(DealType.STANDARD)
            assertThat(fetchedSchool?.address?.postcode).isEqualTo("12345")
            assertThat(fetchedSchool?.district?.name).isEqualTo("good stuff")
            assertThat(fetchedSchool?.district?.deal?.type).isEqualTo(DealType.STANDARD)
        }

        @Test
        fun `persists a school with an expiry date`() {
            val accessExpiresOn = ZonedDateTime.now().plusDays(1)
            val school = organisationRepository.save(
                OrganisationFactory.school(
                    address = Address(
                        postcode = "12345"
                    ),
                    deal = deal(
                        accessExpiresOn = accessExpiresOn
                    )
                )
            )
            val fetchedSchool = organisationRepository.findSchoolById(school.id)

            assertThat(fetchedSchool?.id).isNotNull
            assertThat(fetchedSchool?.deal?.type).isEqualTo(DealType.STANDARD)
            assertThat(fetchedSchool?.address?.postcode).isEqualTo("12345")
            assertThat(fetchedSchool?.deal?.accessExpiresOn).isEqualTo(accessExpiresOn)
        }

        @Test
        fun `persists the expiry date from the parent organisation`() {
            val accessExpiresOn = ZonedDateTime.now().plusDays(1)
            val district = organisationRepository.save(
                OrganisationFactory.district(
                    name = "good stuff",
                    deal = deal(
                        accessExpiresOn = accessExpiresOn
                    )
                )
            )
            val school = organisationRepository.save(
                OrganisationFactory.school(
                    district = district,
                    address = Address(
                        postcode = "12345"
                    )
                )
            )

            val fetchedSchoolAccount = organisationRepository.findSchoolById(school.id)

            assertThat(fetchedSchoolAccount?.id).isNotNull
            assertThat(fetchedSchoolAccount?.deal?.type).isEqualTo(DealType.STANDARD)
            assertThat(fetchedSchoolAccount?.address?.postcode).isEqualTo("12345")
            assertThat(fetchedSchoolAccount?.district?.deal?.accessExpiresOn).isEqualTo(accessExpiresOn)
        }

        @Test
        fun `update changes nested redundant copies of an organisation when it is changed`() {
            val district = organisationRepository.save(
                OrganisationFactory.district()
            )
            val school = organisationRepository.save(
                OrganisationFactory.school(district = district, name = "school name")
            )

            organisationRepository.update(
                district.id,
                ReplaceDealType(DealType.DESIGN_PARTNER)
            )

            val schoolAfterDistrictUpdate = organisationRepository.findSchoolById(school.id)

            assertThat(schoolAfterDistrictUpdate?.district?.deal?.type).isEqualTo(DealType.DESIGN_PARTNER)
            assertThat(schoolAfterDistrictUpdate?.name).isEqualTo("school name")
        }
    }

    @Nested
    inner class FindOrganisations {
        @Test
        fun `looks up an organisation by associated role`() {
            val role = "ROLE_VIEWSONIC"
            val organisation = organisationRepository.save(
                OrganisationFactory.apiIntegration(
                    role = role
                )
            )

            val foundOrganisation = organisationRepository.findApiIntegrationByRole(role)
            assertThat(organisation).isEqualTo(foundOrganisation)
        }

        @Test
        fun `looks up an organisation by id`() {
            val organisation =
                organisationRepository.save(OrganisationFactory.apiIntegration())

            val foundOrganisation = organisationRepository.findOrganisationById(organisation.id)

            assertThat(organisation).isEqualTo(foundOrganisation)
        }

        @Test
        fun `looks up schools by name and country`() {
            val correctSchool = organisationRepository.save(
                OrganisationFactory.school(
                    name = "Some School",
                    address = Address(
                        country = Country.fromCode("GBR")
                    )
                )
            )
            organisationRepository.save(
                OrganisationFactory.apiIntegration(name = "Some School")
            )
            organisationRepository.save(
                OrganisationFactory.school(
                    name = "Some School",
                    address = Address(
                        country = Country.fromCode("POL")
                    )
                )
            )
            organisationRepository.save(
                OrganisationFactory.school(
                    name = "Another one",
                    address = Address(
                        country = Country.fromCode("GBR")
                    )
                )
            )

            val schools = organisationRepository.lookupSchools(
                schoolName = "school",
                countryCode = "GBR"
            )

            assertThat(schools).hasSize(1)
            assertThat(schools.first().id).isEqualTo(correctSchool.id)
        }

        @Test
        fun `looks up an api integration by name`() {
            val organisation = organisationRepository.save(
                OrganisationFactory.apiIntegration(name = "api-name")
            )

            val retrievedOrganisation = organisationRepository.findApiIntegrationByName(name = "api-name")

            assertThat(organisation).isEqualTo(retrievedOrganisation)
        }

        @Test
        fun `find school by external id`() {
            val school = organisationRepository.save(
                OrganisationFactory.school(externalId = ExternalOrganisationId("external-id"))
            )
            val retrievedOrganisation =
                organisationRepository.findOrganisationByExternalId(ExternalOrganisationId("external-id"))

            assertThat(school).isEqualTo(retrievedOrganisation)
        }

        @Test
        fun `find organisations by parent id`() {
            val district = organisationRepository.save(OrganisationFactory.district())
            organisationRepository.save(OrganisationFactory.school(district = district))
            organisationRepository.save(OrganisationFactory.school(district = null))
            organisationRepository.save(OrganisationFactory.school(district = null))

            assertThat(organisationRepository.findSchools()).hasSize(3)
            assertThat(organisationRepository.findOrganisationsByParentId(district.id)).hasSize(1)
        }

        @Test
        fun `find independent schools and districts by country code`() {
            val district = organisationRepository.save(OrganisationFactory.district())

            val schoolUsaNoDistrict = organisationRepository.save(
                OrganisationFactory.school(
                    district = null,
                    address = Address(
                        country = Country.fromCode(Country.USA_ISO)
                    )
                )
            )

            organisationRepository.save(
                OrganisationFactory.school(
                    district = district,
                    address = Address(
                        country = Country.fromCode(Country.USA_ISO)
                    )
                )
            )

            organisationRepository.save(
                OrganisationFactory.school(
                    district = null,
                    address = Address(
                        country = Country.fromCode("GBR")
                    )
                )
            )

            organisationRepository.save(
                OrganisationFactory.apiIntegration(
                    address = Address(
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
            assertThat(independentOrganisations).containsExactly(district, schoolUsaNoDistrict)
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
        fun `find schools`() {
            val school =
                organisationRepository.save(OrganisationFactory.school())
            organisationRepository.save(OrganisationFactory.district())
            organisationRepository.save(OrganisationFactory.apiIntegration())

            val allSchools = organisationRepository.findSchools()

            assertThat(allSchools).containsExactlyInAnyOrder(school)
        }

        @Test
        fun `find organisation by name`() {
            organisationRepository.save(OrganisationFactory.school(name = "cool school"))
            organisationRepository.save(OrganisationFactory.school(name = "bad school"))

            val organisations = organisationRepository.findOrganisations(
                name = "cOol",
                countryCode = null,
                types = null,
                page = 0,
                size = 10
            )

            assertThat(organisations).hasSize(1)
            assertThat(organisations.first().name).isEqualTo("cool school")
        }
    }

    @Nested
    inner class UpdateOrganisation {
        @Test
        fun `account type update`() {
            val organisation = organisationRepository.save(OrganisationFactory.district())

            assertThat(organisation.deal.type).isEqualTo(DealType.STANDARD)

            val updatedOrganisation = organisationRepository.update(
                organisation.id,
                ReplaceDealType(DealType.DESIGN_PARTNER)
            )

            assertThat(updatedOrganisation).isNotNull
            assertThat(updatedOrganisation?.deal?.type).isEqualTo(DealType.DESIGN_PARTNER)
            assertThat(organisationRepository.findOrganisationById(organisation.id)?.deal?.type).isEqualTo(
                DealType.DESIGN_PARTNER
            )
        }

        @Test
        fun `account access expiry update`() {
            val oldExpiry = ZonedDateTime.now()
            val newExpiry = ZonedDateTime.now().plusWeeks(2)
            val organisation = organisationRepository.save(
                OrganisationFactory.school(
                    deal = deal(
                        accessExpiresOn = oldExpiry
                    )
                )
            )

            val updatedOrganisation = organisationRepository.update(
                organisation.id,
                ReplaceExpiryDate(newExpiry)
            )

            assertThat(updatedOrganisation?.deal?.accessExpiresOn).isEqualTo(newExpiry)
        }

        @Test
        fun `update multiple properties`() {
            val organisation = organisationRepository.save(OrganisationFactory.school())

            val accessExpiresOn = ZonedDateTime.parse("2012-08-08T00:00:00Z")
            val updatedOrganisation = organisationRepository.update(
                organisation.id,
                ReplaceDomain("some-domain"),
                ReplaceExpiryDate(accessExpiresOn)
            )

            assertThat(updatedOrganisation?.domain).isEqualTo("some-domain")
            assertThat(updatedOrganisation?.deal?.accessExpiresOn).isEqualTo(accessExpiresOn)
        }

        @Test
        fun `update returns null when organisation not found`() {
            val updatedOrganisation = organisationRepository.update(
                OrganisationId(),
                ReplaceDealType(DealType.DESIGN_PARTNER)
            )
            assertThat(updatedOrganisation).isNull()
        }
    }

    @Nested
    inner class OrderAndPagination {
        @Test
        fun `ordering independent organisations by expiry date, then name`() {
            val schoolOne = organisationRepository.save(
                OrganisationFactory.school(
                    name = "schoolA",
                    district = null,
                    address = Address(
                        country = Country.fromCode(Country.USA_ISO)
                    ),
                    deal = deal(
                        accessExpiresOn = ZonedDateTime.now().plusDays(60)
                    )
                )
            )

            val schoolTwo = organisationRepository.save(
                OrganisationFactory.school(
                    name = "schoolB",
                    district = null,
                    address = Address(
                        country = Country.fromCode(Country.USA_ISO)
                    ),
                    deal = deal(
                        accessExpiresOn = ZonedDateTime.now().plusDays(5)
                    )
                )
            )

            val schoolThree = organisationRepository.save(
                OrganisationFactory.school(
                    name = "schoolC",
                    district = null,
                    address = Address(
                        country = Country.fromCode(Country.USA_ISO)
                    ),
                    deal = deal(
                        accessExpiresOn = ZonedDateTime.now().plusDays(10)
                    )
                )
            )

            val schoolSix = organisationRepository.save(
                OrganisationFactory.school(
                    name = "schoolF",
                    district = null,
                    address = Address(
                        country = Country.fromCode(Country.USA_ISO)
                    )
                )
            )
            val schoolFour = organisationRepository.save(
                OrganisationFactory.school(
                    name = "schoolE",
                    district = null,
                    address = Address(
                        country = Country.fromCode(Country.USA_ISO)
                    )
                )
            )

            val schoolFive = organisationRepository.save(
                OrganisationFactory.school(
                    name = "schoolD",
                    district = null,
                    address = Address(
                        country = Country.fromCode(Country.USA_ISO)
                    )
                )
            )

            val independentOrganisations: Page<Organisation>? =
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
                OrganisationFactory.school(
                    name = "schoolA",
                    district = null,
                    address = Address(
                        country = Country.fromCode(Country.USA_ISO)
                    ),
                    deal = deal(
                        accessExpiresOn = ZonedDateTime.now().plusDays(60)
                    )
                )
            )

            organisationRepository.save(
                OrganisationFactory.school(
                    name = "schoolB",
                    district = null,
                    address = Address(
                        country = Country.fromCode(Country.USA_ISO)
                    ),
                    deal = deal(
                        accessExpiresOn = ZonedDateTime.now().plusDays(5)
                    )
                )
            )

            val schoolThree = organisationRepository.save(
                OrganisationFactory.school(
                    name = "schoolC",
                    district = null,
                    address = Address(
                        country = Country.fromCode(Country.USA_ISO)
                    ),
                    deal = deal(
                        accessExpiresOn = ZonedDateTime.now().plusDays(10)
                    )
                )
            )

            organisationRepository.save(
                OrganisationFactory.school(
                    name = "schoolF",
                    district = null,
                    address = Address(
                        country = Country.fromCode(Country.USA_ISO)
                    )
                )
            )
            organisationRepository.save(
                OrganisationFactory.school(
                    name = "schoolE",
                    district = null,
                    address = Address(
                        country = Country.fromCode(Country.USA_ISO)
                    )
                )
            )

            organisationRepository.save(
                OrganisationFactory.school(
                    name = "schoolD",
                    district = null,
                    address = Address(
                        country = Country.fromCode(Country.USA_ISO)
                    )
                )
            )

            val independentOrganisations: Page<Organisation>? =
                organisationRepository.findOrganisations(
                    countryCode = Country.USA_ISO,
                    page = 0,
                    size = 2,
                    types = listOf(OrganisationType.SCHOOL, OrganisationType.DISTRICT)
                )

            assertThat(independentOrganisations).containsExactly(schoolOne, schoolThree)
            assertThat(independentOrganisations?.pageSize).isEqualTo(2)
            assertThat(independentOrganisations?.totalElements).isEqualTo(6)
        }
    }
}
