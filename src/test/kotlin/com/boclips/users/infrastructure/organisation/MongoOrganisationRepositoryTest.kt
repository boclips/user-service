package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.accessrules.AccessRuleId
import com.boclips.users.domain.model.account.Organisation
import com.boclips.users.domain.model.account.OrganisationId
import com.boclips.users.domain.model.account.AccountType
import com.boclips.users.domain.model.account.ApiIntegration
import com.boclips.users.domain.model.account.OrganisationType
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.service.AccountExpiresOnUpdate
import com.boclips.users.domain.service.AccountTypeUpdate
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

        val organisation: Organisation<ApiIntegration> = accountRepository.save(
            apiIntegration = OrganisationDetailsFactory.apiIntegration(
                name = organisationName,
                allowsOverridingUserIds = true
            ),
            accessRuleIds = accessRulesIds
        )

        assertThat(organisation.id).isNotNull
        assertThat(organisation.type).isEqualTo(AccountType.STANDARD)
        assertThat(organisation.organisation.name).isEqualTo(organisationName)
        assertThat(organisation.accessRuleIds).isEqualTo(accessRulesIds)
        assertThat((organisation).organisation.allowsOverridingUserIds).isTrue()
    }

    @Test
    fun `persists a school with an existing district`() {
        val district = accountRepository.save(
            OrganisationDetailsFactory.district(name = "good stuff")
        )
        val school = accountRepository.save(
            OrganisationDetailsFactory.school(district = district, postCode = "12345")
        )
        val fetchedSchool = accountRepository.findSchoolById(school.id)

        assertThat(fetchedSchool?.id).isNotNull
        assertThat(fetchedSchool?.type).isEqualTo(AccountType.STANDARD)
        assertThat(fetchedSchool?.organisation?.postcode).isEqualTo("12345")
        assertThat(fetchedSchool?.organisation?.district?.organisation?.name).isEqualTo("good stuff")
        assertThat(fetchedSchool?.organisation?.district?.type).isEqualTo(AccountType.STANDARD)
        assertThat(fetchedSchool?.organisation?.district?.organisation?.name).isEqualTo("good stuff")
    }

    @Test
    fun `persists a school with an expiry date`() {
        val accessExpiresOn = ZonedDateTime.now().plusDays(1)
        val school = accountRepository.save(
            school = OrganisationDetailsFactory.school(postCode = "12345"), accessExpiresOn = accessExpiresOn
        )
        val fetchedSchool = accountRepository.findSchoolById(school.id)

        assertThat(fetchedSchool?.id).isNotNull
        assertThat(fetchedSchool?.type).isEqualTo(AccountType.STANDARD)
        assertThat(fetchedSchool?.organisation?.postcode).isEqualTo("12345")
        assertThat(fetchedSchool?.accessExpiresOn).isEqualTo(accessExpiresOn)
    }

    @Test
    fun `persists the expiry date from the parent organisation`() {
        val accessExpiresOn = ZonedDateTime.now().plusDays(1)
        val district = accountRepository.save(
            district = OrganisationDetailsFactory.district(name = "good stuff"), accessExpiresOn = accessExpiresOn
        )
        val school = accountRepository.save(
            school = OrganisationDetailsFactory.school(district = district, postCode = "12345")
        )
        val fetchedSchoolAccount = accountRepository.findSchoolById(school.id)

        assertThat(fetchedSchoolAccount?.id).isNotNull
        assertThat(fetchedSchoolAccount?.type).isEqualTo(AccountType.STANDARD)
        assertThat(fetchedSchoolAccount?.organisation?.postcode).isEqualTo("12345")
        assertThat(fetchedSchoolAccount?.organisation?.district?.accessExpiresOn).isEqualTo(accessExpiresOn)
    }

    @Test
    fun `looks up an organisation by associated role`() {
        val role = "ROLE_VIEWSONIC"
        val organisation = accountRepository.save(
            apiIntegration = OrganisationDetailsFactory.apiIntegration(),
            role = role
        )

        val foundOrganisation = accountRepository.findApiIntegrationByRole(role)
        assertThat(organisation).isEqualTo(foundOrganisation)
    }

    @Test
    fun `looks up an organisation by id`() {
        val organisation =
            accountRepository.save(apiIntegration = OrganisationDetailsFactory.apiIntegration())

        val foundOrganisation = accountRepository.findAccountById(organisation.id)

        assertThat(organisation).isEqualTo(foundOrganisation)
    }

    @Test
    fun `looks up schools by name and country`() {
        val correctSchool = accountRepository.save(
            OrganisationDetailsFactory.school(name = "Some School", countryName = "GBR")
        )
        accountRepository.save(
            apiIntegration = OrganisationDetailsFactory.apiIntegration(name = "Some School")
        )
        accountRepository.save(
            OrganisationDetailsFactory.school(name = "Some School", countryName = "POL")
        )
        accountRepository.save(
            OrganisationDetailsFactory.school(name = "Another one", countryName = "GBR")
        )

        val schools = accountRepository.lookupSchools(
            schoolName = "school",
            countryCode = "GBR"
        )

        assertThat(schools).hasSize(1)
        assertThat(schools.first().id).isEqualTo(correctSchool.id.value)
    }

    @Test
    fun `looks up an api integration by name`() {
        val organisation = accountRepository.save(
            apiIntegration = OrganisationDetailsFactory.apiIntegration(name = "api-name")
        )

        val retrievedOrganisation = accountRepository.findApiIntegrationByName(name = "api-name")

        assertThat(organisation).isEqualTo(retrievedOrganisation)
    }

    @Test
    fun `find school by external id`() {
        val school = accountRepository.save(
            school = OrganisationDetailsFactory.school(externalId = "external-id")
        )

        val retrievedOrganisation = accountRepository.findAccountByExternalId("external-id")

        assertThat(school).isEqualTo(retrievedOrganisation)
    }

    @Test
    fun `find schools`() {
        val school = accountRepository.save(OrganisationDetailsFactory.school())
        accountRepository.save(OrganisationDetailsFactory.district())
        accountRepository.save(OrganisationDetailsFactory.apiIntegration())

        val allSchools = accountRepository.findSchools()

        assertThat(allSchools).containsExactlyInAnyOrder(school)
    }

    @Test
    fun `account type update`() {
        val organisation = accountRepository.save(OrganisationDetailsFactory.district())

        assertThat(organisation.type).isEqualTo(AccountType.STANDARD)

        val updatedOrganisation = accountRepository.update(
            AccountTypeUpdate(
                id = organisation.id,
                type = AccountType.DESIGN_PARTNER
            )
        )

        assertThat(updatedOrganisation).isNotNull
        assertThat(updatedOrganisation?.type).isEqualTo(AccountType.DESIGN_PARTNER)
        assertThat(accountRepository.findAccountById(organisation.id)?.type).isEqualTo(
            AccountType.DESIGN_PARTNER
        )
    }

    @Test
    fun `account access expiry update`() {
        val oldExpiry = ZonedDateTime.now()
        val newExpiry = ZonedDateTime.now().plusWeeks(2)
        val organisation = accountRepository.save(OrganisationDetailsFactory.school(), accessExpiresOn = oldExpiry)

        val updatedOrganisation = accountRepository.update(
            AccountExpiresOnUpdate(
                id = organisation.id,
                accessExpiresOn = newExpiry
            )
        )

        assertThat(updatedOrganisation?.accessExpiresOn).isEqualTo(newExpiry)
    }

    @Test
    fun `update returns null when organisation not found`() {
        val updatedOrganisation = accountRepository.update(
            AccountTypeUpdate(
                id = OrganisationId("doesnotexist"),
                type = AccountType.DESIGN_PARTNER
            )
        )
        assertThat(updatedOrganisation).isNull()
    }

    @Test
    fun `find organisations by parent id`() {
        val district = accountRepository.save(OrganisationDetailsFactory.district())
        accountRepository.save(OrganisationDetailsFactory.school(district = district))
        accountRepository.save(OrganisationDetailsFactory.school(district = null))
        accountRepository.save(OrganisationDetailsFactory.school(district = null))

        assertThat(accountRepository.findSchools()).hasSize(3)
        assertThat(accountRepository.findAccountsByParentId(district.id)).hasSize(1)
    }

    @Test
    fun `find independent schools and districts by country code`() {
        val district = accountRepository.save(OrganisationDetailsFactory.district())
        val school = accountRepository.save(
            OrganisationDetailsFactory.school(
                district = null,
                country = Country.fromCode(Country.USA_ISO)
            )
        )

        accountRepository.save(
            OrganisationDetailsFactory.school(
                district = district,
                country = Country.fromCode(Country.USA_ISO)
            )
        )

        accountRepository.save(
            OrganisationDetailsFactory.school(
                district = null,
                country = Country.fromCode("GBR")
            )
        )

        accountRepository.save(
            OrganisationDetailsFactory.apiIntegration(
                country = Country.fromCode(Country.USA_ISO)
            )
        )

        val independentOrganisations =
            accountRepository.findAccounts(
                countryCode = Country.USA_ISO,
                page = 0,
                size = 10,
                types = listOf(OrganisationType.SCHOOL, OrganisationType.DISTRICT)
            )
        assertThat(independentOrganisations).containsExactly(district, school)
        assertThat(independentOrganisations).hasSize(2)

        assertThat(
            accountRepository.findAccounts(
                countryCode = "GBR",
                page = 0,
                size = 10,
                types = listOf(OrganisationType.SCHOOL, OrganisationType.DISTRICT)
            )
        ).hasSize(1)
    }

    @Test
    fun `ordering independent organisations by expiry date, then name`() {
        val schoolOne = accountRepository.save(
            OrganisationDetailsFactory.school(
                name = "schoolA",
                district = null,
                country = Country.fromCode(Country.USA_ISO)
            ),
            ZonedDateTime.now().plusDays(60)
        )

        val schoolTwo = accountRepository.save(
            OrganisationDetailsFactory.school(
                name = "schoolB",
                district = null,
                country = Country.fromCode(Country.USA_ISO)
            ),
            ZonedDateTime.now().plusDays(5)
        )

        val schoolThree = accountRepository.save(
            OrganisationDetailsFactory.school(
                name = "schoolC",
                district = null,
                country = Country.fromCode(Country.USA_ISO)
            ),
            ZonedDateTime.now().plusDays(10)
        )

        val schoolSix = accountRepository.save(
            OrganisationDetailsFactory.school(
                name = "schoolF",
                district = null,
                country = Country.fromCode(Country.USA_ISO)
            )
        )
        val schoolFour = accountRepository.save(
            OrganisationDetailsFactory.school(
                name = "schoolE",
                district = null,
                country = Country.fromCode(Country.USA_ISO)
            )
        )

        val schoolFive = accountRepository.save(
            OrganisationDetailsFactory.school(
                name = "schoolD",
                district = null,
                country = Country.fromCode(Country.USA_ISO)
            )
        )

        val independentOrganisations: Page<Organisation<*>>? =
            accountRepository.findAccounts(
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
        val schoolOne = accountRepository.save(
            OrganisationDetailsFactory.school(
                name = "schoolA",
                district = null,
                country = Country.fromCode(Country.USA_ISO)
            ),
            ZonedDateTime.now().plusDays(60)
        )

        accountRepository.save(
            OrganisationDetailsFactory.school(
                name = "schoolB",
                district = null,
                country = Country.fromCode(Country.USA_ISO)
            ),
            ZonedDateTime.now().plusDays(5)
        )

        val schoolThree = accountRepository.save(
            OrganisationDetailsFactory.school(
                name = "schoolC",
                district = null,
                country = Country.fromCode(Country.USA_ISO)
            ),
            ZonedDateTime.now().plusDays(10)
        )

        accountRepository.save(
            OrganisationDetailsFactory.school(
                name = "schoolF",
                district = null,
                country = Country.fromCode(Country.USA_ISO)
            )
        )
        accountRepository.save(
            OrganisationDetailsFactory.school(
                name = "schoolE",
                district = null,
                country = Country.fromCode(Country.USA_ISO)
            )
        )

        accountRepository.save(
            OrganisationDetailsFactory.school(
                name = "schoolD",
                district = null,
                country = Country.fromCode(Country.USA_ISO)
            )
        )

        val independentOrganisations: Page<Organisation<*>>? =
            accountRepository.findAccounts(
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
