package com.boclips.users.infrastructure.organisation

import com.boclips.security.utils.Client
import com.boclips.users.domain.model.Page
import com.boclips.users.domain.model.access.ContentPackageId
import com.boclips.users.domain.model.organisation.Address
import com.boclips.users.domain.model.organisation.ApiIntegration
import com.boclips.users.domain.model.organisation.ContentAccess
import com.boclips.users.domain.model.organisation.ExternalOrganisationId
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.organisation.OrganisationTag.DEFAULT_ORGANISATION
import com.boclips.users.domain.model.organisation.OrganisationTag.DESIGN_PARTNER
import com.boclips.users.domain.model.organisation.OrganisationType
import com.boclips.users.domain.model.organisation.OrganisationUpdate.AddTag
import com.boclips.users.domain.model.organisation.OrganisationUpdate.ReplaceBilling
import com.boclips.users.domain.model.organisation.OrganisationUpdate.ReplaceContentPackageId
import com.boclips.users.domain.model.organisation.OrganisationUpdate.ReplaceDomain
import com.boclips.users.domain.model.organisation.OrganisationUpdate.ReplaceExpiryDate
import com.boclips.users.domain.model.school.Country
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.users.testsupport.factories.OrganisationFactory.Companion.apiIntegration
import com.boclips.users.testsupport.factories.OrganisationFactory.Companion.deal
import com.boclips.users.testsupport.factories.OrganisationFactory.Companion.district
import com.boclips.users.testsupport.factories.OrganisationFactory.Companion.school
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

            val organisation: ApiIntegration = apiIntegration(
                name = organisationName,
                deal = deal(
                    contentAccess = ContentAccess.SimpleAccess(ContentPackageId("123"))
                ),
                allowsOverridingUserId = true,
                logoUrl = "www.great-logo.com"
            )

            val retrievedOrganisation = organisationRepository.save(organisation)

            assertThat(retrievedOrganisation.id).isNotNull
            assertThat(retrievedOrganisation.name).isEqualTo(organisationName)
            assertThat(retrievedOrganisation.allowsOverridingUserIds).isTrue()
            assertThat((retrievedOrganisation.deal.contentAccess as? ContentAccess.SimpleAccess)?.id?.value).isEqualTo("123")
            assertThat(retrievedOrganisation.logoUrl).isEqualTo("www.great-logo.com")
        }

        @Test
        fun `persists a school with an existing district`() {
            val district = organisationRepository.save(
                district(name = "good stuff")
            )
            val school = organisationRepository.save(
                school(
                    district = district,
                    address = Address(
                        postcode = "12345"
                    )
                )
            )
            val fetchedSchool = organisationRepository.findSchoolById(school.id)

            assertThat(fetchedSchool?.id).isNotNull
            assertThat(fetchedSchool?.address?.postcode).isEqualTo("12345")
            assertThat(fetchedSchool?.district?.name).isEqualTo("good stuff")
        }

        @Test
        fun `persists a school with an expiry date`() {
            val accessExpiresOn = ZonedDateTime.now().plusDays(1)
            val school = organisationRepository.save(
                school(
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
            assertThat(fetchedSchool?.address?.postcode).isEqualTo("12345")
            assertThat(fetchedSchool?.deal?.accessExpiresOn).isEqualToIgnoringNanos(accessExpiresOn)
        }

        @Test
        fun `persists the expiry date from the parent organisation`() {
            val accessExpiresOn = ZonedDateTime.now().plusDays(1)
            val district = organisationRepository.save(
                district(
                    name = "good stuff",
                    deal = deal(
                        accessExpiresOn = accessExpiresOn
                    )
                )
            )
            val school = organisationRepository.save(
                school(
                    district = district,
                    address = Address(
                        postcode = "12345"
                    )
                )
            )

            val fetchedSchoolAccount = organisationRepository.findSchoolById(school.id)

            assertThat(fetchedSchoolAccount?.id).isNotNull
            assertThat(fetchedSchoolAccount?.address?.postcode).isEqualTo("12345")
            assertThat(fetchedSchoolAccount?.district?.deal?.accessExpiresOn).isEqualToIgnoringNanos(accessExpiresOn)
        }

        @Test
        fun `update changes nested redundant copies of an organisation when it is changed`() {
            val district = organisationRepository.save(
                district()
            )
            val school = organisationRepository.save(
                school(district = district, name = "school name")
            )

            val expiry = ZonedDateTime.now()
            organisationRepository.update(
                district.id,
                ReplaceExpiryDate(expiry)
            )

            val schoolAfterDistrictUpdate = organisationRepository.findSchoolById(school.id)

            assertThat(schoolAfterDistrictUpdate?.district?.deal?.accessExpiresOn).isEqualToIgnoringNanos(expiry)
            assertThat(schoolAfterDistrictUpdate?.name).isEqualTo("school name")
        }

        @Test
        fun `can update the contentPackageId field`() {
            val apiCustomer = organisationRepository.save(
                apiIntegration(deal = deal(contentAccess = ContentAccess.SimpleAccess(ContentPackageId("12345"))))
            )

            organisationRepository.update(
                apiCustomer.id,
                ReplaceContentPackageId(ContentPackageId("5e6f91c75849165c9cfb2a38"))
            )

            assertThat(
                (organisationRepository.findApiIntegrationByName(apiCustomer.name)?.deal?.contentAccess as? ContentAccess.SimpleAccess)?.id
            ).isEqualTo(ContentPackageId("5e6f91c75849165c9cfb2a38"))
        }

        @Test
        fun `can persist a client based content access`() {
            val apiCustomer = organisationRepository.save(
                apiIntegration(
                    deal = deal(
                        contentAccess = ContentAccess.ClientBasedAccess(
                            mapOf(
                                Client.Teachers to ContentPackageId(
                                    "12345"
                                )
                            )
                        )
                    )
                )
            )

            val persistedContentAccess =
                organisationRepository.findApiIntegrationByName(apiCustomer.name)?.deal?.contentAccess as? ContentAccess.ClientBasedAccess

            assertThat((persistedContentAccess!!.clientAccess).size).isEqualTo(1)
            assertThat((persistedContentAccess.clientAccess)[Client.Teachers]).isEqualTo(ContentPackageId("12345"))
        }

        @Test
        fun `can update the billing field`() {
            val apiCustomer = organisationRepository.save(
                apiIntegration(deal = deal(billing = false))
            )

            organisationRepository.update(
                apiCustomer.id,
                ReplaceBilling(true)
            )

            assertThat(
                organisationRepository.findApiIntegrationByName(apiCustomer.name)?.deal?.billing
            ).isEqualTo(true)
        }
    }

    @Test
    fun `find by tag`() {
        organisationRepository.save(district(name = "Default", tags = setOf(DEFAULT_ORGANISATION)))
        organisationRepository.save(district(name = "Another", tags = setOf(DESIGN_PARTNER)))

        assertThat(organisationRepository.findByTag(DEFAULT_ORGANISATION)).hasSize(1)
        assertThat(organisationRepository.findByTag(DEFAULT_ORGANISATION).first().name).isEqualTo("Default")
    }

    @Test
    fun `find by email domain`() {
        organisationRepository.save(district(name = "A", domain = "domain.com"))
        organisationRepository.save(district(name = "B", domain = "another.com"))
        organisationRepository.save(district(name = "C", domain = null))

        assertThat(organisationRepository.findByEmailDomain("domain.com")).hasSize(1)
        assertThat(organisationRepository.findByEmailDomain("domain.com").first().name).isEqualTo("A")
    }

    @Nested
    inner class FindOrganisations {
        @Test
        fun `looks up an organisation by associated role`() {
            val organisation1 = organisationRepository.save(apiIntegration(role = "ROLE_1"))
            val organisation2 = organisationRepository.save(apiIntegration(role = "ROLE_2"))

            val foundOrganisations = organisationRepository.findByRoleIn(listOf("ROLE_1", "ROLE_2", "ROLE_3"))
            assertThat(foundOrganisations).containsExactly(organisation1, organisation2)
        }

        @Test
        fun `looks up an organisation by id`() {
            val organisation = organisationRepository.save(apiIntegration())

            val foundOrganisation = organisationRepository.findOrganisationById(organisation.id)

            assertThat(organisation).isEqualTo(foundOrganisation)
        }

        @Test
        fun `looks up schools by name and country`() {
            val correctSchool = organisationRepository.save(
                school(
                    name = "Some School",
                    address = Address(
                        country = Country.fromCode("GBR")
                    )
                )
            )
            organisationRepository.save(
                apiIntegration(name = "Some School")
            )
            organisationRepository.save(
                school(
                    name = "Some School",
                    address = Address(
                        country = Country.fromCode("POL")
                    )
                )
            )
            organisationRepository.save(
                school(
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
        fun `allows for finding organisations that have parent district`() {
            val parentDistrict = organisationRepository.save(
                district(name = "api-name")
            )

            val correctSchool = organisationRepository.save(
                school(
                    name = "happy school",
                    address = Address(
                        country = Country.fromCode("GBR")
                    ),
                    district = parentDistrict
                )
            )

            val schools = organisationRepository.findOrganisations(
                name = "happy",
                page = 0,
                size = 10,
                types = null
            )

            assertThat(schools).hasSize(1)
            assertThat(schools.first().id).isEqualTo(correctSchool.id)
        }

        @Test
        fun `looks up an api integration by name`() {
            val organisation = organisationRepository.save(
                apiIntegration(name = "api-name")
            )

            val retrievedOrganisation = organisationRepository.findApiIntegrationByName(name = "api-name")

            assertThat(organisation).isEqualTo(retrievedOrganisation)
        }

        @Test
        fun `find school by external id`() {
            val school = organisationRepository.save(
                school(externalId = ExternalOrganisationId("external-id"))
            )
            val retrievedOrganisation =
                organisationRepository.findOrganisationByExternalId(ExternalOrganisationId("external-id"))

            assertThat(school).isEqualTo(retrievedOrganisation)
        }

        @Test
        fun `find organisations by parent id`() {
            val district = organisationRepository.save(district())
            organisationRepository.save(school(district = district))
            organisationRepository.save(school(district = null))
            organisationRepository.save(school(district = null))

            assertThat(organisationRepository.findOrganisationsByParentId(district.id)).hasSize(1)
        }

        @Test
        fun `find schools and districts by country code`() {
            val district = organisationRepository.save(district())

            val schoolUsaNoDistrict = organisationRepository.save(
                school(
                    district = null,
                    address = Address(
                        country = Country.fromCode(Country.USA_ISO)
                    )
                )
            )

            val schoolUsaWithDistrict = organisationRepository.save(
                school(
                    district = district,
                    address = Address(
                        country = Country.fromCode(Country.USA_ISO)
                    )
                )
            )

            organisationRepository.save(
                school(
                    district = null,
                    address = Address(
                        country = Country.fromCode("GBR")
                    )
                )
            )

            organisationRepository.save(
                apiIntegration(
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
            assertThat(independentOrganisations).containsExactly(district, schoolUsaNoDistrict, schoolUsaWithDistrict)
            assertThat(independentOrganisations).hasSize(3)

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
        fun `find organisation by name`() {
            organisationRepository.save(school(name = "cool school"))
            organisationRepository.save(school(name = "bad school"))

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

        @Test
        fun `find organisations that have custom pricing`() {
            val customPriceOrg = organisationRepository.save(
                apiIntegration(
                    deal = OrganisationFactory.pricedDeal()
                )
            )
            organisationRepository.save(
                apiIntegration(
                    deal = deal()
                )
            )

            val orgs =
                organisationRepository.findOrganisations(hasCustomPrices = true, size = 10, page = 0, types = null)

            assertThat(orgs.items).hasSize(1)
            assertThat(orgs.items.first().id).isEqualTo(customPriceOrg.id)
        }

        @Test
        fun `find organisations that does have not custom pricing`() {
            organisationRepository.save(
                apiIntegration(
                    deal = OrganisationFactory.pricedDeal()
                )
            )
            val noCustomPricesOrg = organisationRepository.save(
                apiIntegration(
                    deal = deal()
                )
            )

            val orgs =
                organisationRepository.findOrganisations(hasCustomPrices = false, size = 10, page = 0, types = null)

            assertThat(orgs.items).hasSize(1)
            assertThat(orgs.items.first().id).isEqualTo(noCustomPricesOrg.id)
        }

        @Test
        fun `finds an organisation by a legacy organisation ID`() {
            val idToLookFor = "anIdOfOrgFromLegacyPublishersApp"
            organisationRepository.save(
                school(
                    legacyId = idToLookFor
                )
            )
            organisationRepository.save(
                school(
                    legacyId = "someOtherOrgFromLegacyPublishersApp"
                )
            )

            val organisation = organisationRepository.findByLegacyId(idToLookFor)

            assertThat(organisation!!.legacyId).isNotNull()
            assertThat(organisation.legacyId).isEqualTo(idToLookFor)
        }
    }

    @Nested
    inner class UpdateOrganisation {

        @Test
        fun `account access expiry update`() {
            val oldExpiry = ZonedDateTime.now()
            val newExpiry = ZonedDateTime.now().plusWeeks(2)
            val organisation = organisationRepository.save(
                school(
                    deal = deal(
                        accessExpiresOn = oldExpiry
                    )
                )
            )

            val updatedOrganisation = organisationRepository.update(
                organisation.id,
                ReplaceExpiryDate(newExpiry)
            )

            assertThat(updatedOrganisation?.deal?.accessExpiresOn).isEqualToIgnoringNanos(newExpiry)
        }

        @Test
        fun `update multiple properties`() {
            val organisation = organisationRepository.save(school())

            val accessExpiresOn = ZonedDateTime.parse("2012-08-08T00:00:00Z")
            val updatedOrganisation = organisationRepository.update(
                organisation.id,
                ReplaceDomain("some-domain"),
                ReplaceExpiryDate(accessExpiresOn),
                AddTag(DESIGN_PARTNER),
                ReplaceBilling(true)
            )

            assertThat(updatedOrganisation?.domain).isEqualTo("some-domain")
            assertThat(updatedOrganisation?.tags).containsExactly(DESIGN_PARTNER)
            assertThat(updatedOrganisation?.deal?.accessExpiresOn).isEqualTo(accessExpiresOn)
            assertThat(updatedOrganisation?.deal?.billing).isTrue()
        }

        @Test
        fun `update returns null when organisation not found`() {
            val updatedOrganisation = organisationRepository.update(
                OrganisationId(),
                ReplaceExpiryDate(ZonedDateTime.now())
            )
            assertThat(updatedOrganisation).isNull()
        }
    }

    @Nested
    inner class OrderAndPagination {
        @Test
        fun `ordering independent organisations by expiry date, then name`() {
            val schoolOne = organisationRepository.save(
                school(
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
                school(
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
                school(
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
                school(
                    name = "schoolF",
                    district = null,
                    address = Address(
                        country = Country.fromCode(Country.USA_ISO)
                    )
                )
            )
            val schoolFour = organisationRepository.save(
                school(
                    name = "schoolE",
                    district = null,
                    address = Address(
                        country = Country.fromCode(Country.USA_ISO)
                    )
                )
            )

            val schoolFive = organisationRepository.save(
                school(
                    name = "schoolD",
                    district = null,
                    address = Address(
                        country = Country.fromCode(Country.USA_ISO)
                    )
                )
            )

            val independentOrganisations: Page<Organisation> =
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
                school(
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
                school(
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
                school(
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
                school(
                    name = "schoolF",
                    district = null,
                    address = Address(
                        country = Country.fromCode(Country.USA_ISO)
                    )
                )
            )
            organisationRepository.save(
                school(
                    name = "schoolE",
                    district = null,
                    address = Address(
                        country = Country.fromCode(Country.USA_ISO)
                    )
                )
            )

            organisationRepository.save(
                school(
                    name = "schoolD",
                    district = null,
                    address = Address(
                        country = Country.fromCode(Country.USA_ISO)
                    )
                )
            )

            val independentOrganisations: Page<Organisation> =
                organisationRepository.findOrganisations(
                    countryCode = Country.USA_ISO,
                    page = 0,
                    size = 2,
                    types = listOf(OrganisationType.SCHOOL, OrganisationType.DISTRICT)
                )

            assertThat(independentOrganisations).containsExactly(schoolOne, schoolThree)
            assertThat(independentOrganisations.pageSize).isEqualTo(2)
            assertThat(independentOrganisations.totalElements).isEqualTo(6)
        }
    }
}
