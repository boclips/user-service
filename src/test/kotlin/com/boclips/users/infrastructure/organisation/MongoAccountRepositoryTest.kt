package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.account.Account
import com.boclips.users.domain.model.account.AccountId
import com.boclips.users.domain.model.account.AccountType
import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.service.AccountExpiresOnUpdate
import com.boclips.users.domain.service.AccountTypeUpdate
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.OrganisationFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Page
import java.time.ZonedDateTime

class MongoAccountRepositoryTest : AbstractSpringIntegrationTest() {

    @Test
    fun `persists an organisation`() {
        val organisationName = "Persist Organisation"

        val contractIds = listOf(ContractId("Contract A"), ContractId("Contract B"))

        val organisationAccount = accountRepository.save(
            apiIntegration = OrganisationFactory.apiIntegration(
                name = organisationName,
                allowsOverridingUserIds = true
            ),
            contractIds = contractIds
        )

        assertThat(organisationAccount.id).isNotNull
        assertThat(organisationAccount.type).isEqualTo(AccountType.STANDARD)
        assertThat(organisationAccount.organisation.name).isEqualTo(organisationName)
        assertThat(organisationAccount.contractIds).isEqualTo(contractIds)
        assertThat((organisationAccount).organisation.allowsOverridingUserIds).isTrue()
    }

    @Test
    fun `persists a school with an existing district`() {
        val district = accountRepository.save(
            OrganisationFactory.district(name = "good stuff")
        )
        val school = accountRepository.save(
            OrganisationFactory.school(district = district, postCode = "12345")
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
            school = OrganisationFactory.school(postCode = "12345"), accessExpiresOn = accessExpiresOn
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
            district = OrganisationFactory.district(name = "good stuff"), accessExpiresOn = accessExpiresOn
        )
        val school = accountRepository.save(
            school = OrganisationFactory.school(district = district, postCode = "12345")
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
            apiIntegration = OrganisationFactory.apiIntegration(),
            role = role
        )

        val foundOrganisation = accountRepository.findApiIntegrationByRole(role)
        assertThat(organisation).isEqualTo(foundOrganisation)
    }

    @Test
    fun `looks up an organisation by id`() {
        val organisation =
            accountRepository.save(apiIntegration = OrganisationFactory.apiIntegration())

        val foundOrganisation = accountRepository.findAccountById(organisation.id)

        assertThat(organisation).isEqualTo(foundOrganisation)
    }

    @Test
    fun `looks up schools by name and country`() {
        val correctSchool = accountRepository.save(
            OrganisationFactory.school(name = "Some School", countryName = "GBR")
        )
        accountRepository.save(
            apiIntegration = OrganisationFactory.apiIntegration(name = "Some School")
        )
        accountRepository.save(
            OrganisationFactory.school(name = "Some School", countryName = "POL")
        )
        accountRepository.save(
            OrganisationFactory.school(name = "Another one", countryName = "GBR")
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
            apiIntegration = OrganisationFactory.apiIntegration(name = "api-name")
        )

        val retrievedOrganisation = accountRepository.findApiIntegrationByName(name = "api-name")

        assertThat(organisation).isEqualTo(retrievedOrganisation)
    }

    @Test
    fun `find school by external id`() {
        val school = accountRepository.save(
            school = OrganisationFactory.school(externalId = "external-id")
        )

        val retrievedOrganisation = accountRepository.findAccountByExternalId("external-id")

        assertThat(school).isEqualTo(retrievedOrganisation)
    }

    @Test
    fun `find schools`() {
        val school = accountRepository.save(OrganisationFactory.school())
        accountRepository.save(OrganisationFactory.district())
        accountRepository.save(OrganisationFactory.apiIntegration())

        val allSchools = accountRepository.findSchools()

        assertThat(allSchools).containsExactlyInAnyOrder(school)
    }

    @Test
    fun `account type update`() {
        val organisation = accountRepository.save(OrganisationFactory.district())

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
        val organisation = accountRepository.save(OrganisationFactory.school(), accessExpiresOn = oldExpiry)

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
                id = AccountId("doesnotexist"),
                type = AccountType.DESIGN_PARTNER
            )
        )
        assertThat(updatedOrganisation).isNull()
    }

    @Test
    fun `find organisations by parent id`() {
        val district = accountRepository.save(OrganisationFactory.district())
        accountRepository.save(OrganisationFactory.school(district = district))
        accountRepository.save(OrganisationFactory.school(district = null))
        accountRepository.save(OrganisationFactory.school(district = null))

        assertThat(accountRepository.findSchools()).hasSize(3)
        assertThat(accountRepository.findAccountsByParentId(district.id)).hasSize(1)
    }

    @Test
    fun `find independent schools and districts by country code`() {
        val district = accountRepository.save(OrganisationFactory.district())
        val school = accountRepository.save(
            OrganisationFactory.school(
                district = null,
                country = Country.fromCode(Country.USA_ISO)
            )
        )

        accountRepository.save(
            OrganisationFactory.school(
                district = district,
                country = Country.fromCode(Country.USA_ISO)
            )
        )

        accountRepository.save(
            OrganisationFactory.school(
                district = null,
                country = Country.fromCode("GBR")
            )
        )

        accountRepository.save(
            OrganisationFactory.apiIntegration(
                country = Country.fromCode(Country.USA_ISO)
            )
        )

        val searchRequestUSA = AccountSearchRequest(
            countryCode = Country.USA_ISO,
            page = 0,
            size = 10
        )
        val independentOrganisations =
            accountRepository.findIndependentSchoolsAndDistricts(searchRequestUSA)
        assertThat(independentOrganisations).containsExactly(district, school)
        assertThat(independentOrganisations).hasSize(2)

        val searchRequestGBR = AccountSearchRequest(
            countryCode = "GBR",
            page = 0,
            size = 10
        )
        assertThat(accountRepository.findIndependentSchoolsAndDistricts(searchRequestGBR)).hasSize(1)
    }

    @Test
    fun `ordering independent organisations by expiry date, then name`() {
        val schoolOne = accountRepository.save(
            OrganisationFactory.school(
                name = "schoolA",
                district = null,
                country = Country.fromCode(Country.USA_ISO)
            ),
            ZonedDateTime.now().plusDays(60)
        )

        val schoolTwo = accountRepository.save(
            OrganisationFactory.school(
                name = "schoolB",
                district = null,
                country = Country.fromCode(Country.USA_ISO)
            ),
            ZonedDateTime.now().plusDays(5)
        )

        val schoolThree = accountRepository.save(
            OrganisationFactory.school(
                name = "schoolC",
                district = null,
                country = Country.fromCode(Country.USA_ISO)
            ),
            ZonedDateTime.now().plusDays(10)
        )

        val schoolSix = accountRepository.save(
            OrganisationFactory.school(
                name = "schoolF",
                district = null,
                country = Country.fromCode(Country.USA_ISO)
            )
        )
        val schoolFour = accountRepository.save(
            OrganisationFactory.school(
                name = "schoolE",
                district = null,
                country = Country.fromCode(Country.USA_ISO)
            )
        )

        val schoolFive = accountRepository.save(
            OrganisationFactory.school(
                name = "schoolD",
                district = null,
                country = Country.fromCode(Country.USA_ISO)
            )
        )
        val searchRequest = AccountSearchRequest(
            countryCode = Country.USA_ISO,
            page = 0,
            size = 6
        )

        val independentOrganisations: Page<Account<*>>? =
            accountRepository.findIndependentSchoolsAndDistricts(searchRequest)
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
            OrganisationFactory.school(
                name = "schoolA",
                district = null,
                country = Country.fromCode(Country.USA_ISO)
            ),
            ZonedDateTime.now().plusDays(60)
        )

        accountRepository.save(
            OrganisationFactory.school(
                name = "schoolB",
                district = null,
                country = Country.fromCode(Country.USA_ISO)
            ),
            ZonedDateTime.now().plusDays(5)
        )

        val schoolThree = accountRepository.save(
            OrganisationFactory.school(
                name = "schoolC",
                district = null,
                country = Country.fromCode(Country.USA_ISO)
            ),
            ZonedDateTime.now().plusDays(10)
        )

        accountRepository.save(
            OrganisationFactory.school(
                name = "schoolF",
                district = null,
                country = Country.fromCode(Country.USA_ISO)
            )
        )
        accountRepository.save(
            OrganisationFactory.school(
                name = "schoolE",
                district = null,
                country = Country.fromCode(Country.USA_ISO)
            )
        )

        accountRepository.save(
            OrganisationFactory.school(
                name = "schoolD",
                district = null,
                country = Country.fromCode(Country.USA_ISO)
            )
        )

        val searchRequest = AccountSearchRequest(
            countryCode = Country.USA_ISO,
            page = 0,
            size = 2
        )

        val independentOrganisations: Page<Account<*>>? =
            accountRepository.findIndependentSchoolsAndDistricts(searchRequest)
        assertThat(independentOrganisations).containsExactly(schoolOne, schoolThree)
        assertThat(independentOrganisations!!.totalPages).isEqualTo(3)
        assertThat(independentOrganisations.totalElements).isEqualTo(6)
    }

    @Test
    fun `find accounts`() {
        val district = accountRepository.save(OrganisationFactory.district())
        val school = accountRepository.save(
            OrganisationFactory.school(
                name = "Independent School",
                district = null,
                country = Country.fromCode(Country.USA_ISO)
            )
        )

        val districtSchool = accountRepository.save(
            OrganisationFactory.school(
                name = "School with District",
                district = district,
                country = Country.fromCode(Country.USA_ISO)
            )
        )

        val usaSchool = accountRepository.save(
            OrganisationFactory.school(
                district = null,
                country = Country.fromCode(Country.USA_ISO)
            )
        )

        val accounts = accountRepository.findAccounts(AccountSearchRequest(page = 0, size = 10, countryCode = null))

        assertThat(accounts).containsExactly(district, school, districtSchool, usaSchool)
        assertThat(accounts).hasSize(4)
    }

    fun `filter accounts for USA organisations`() {
        val district = accountRepository.save(
            OrganisationFactory.district(
                name = "District 9"
            )
        )

        val districtSchool = accountRepository.save(
            OrganisationFactory.school(
                name = "School in District 9",
                district = district,
                country = Country.fromCode(Country.USA_ISO)
            )
        )

        val usaSchool = accountRepository.save(
            OrganisationFactory.school(
                name = "Independent USA School",
                district = null,
                country = Country.fromCode(Country.USA_ISO)
            )
        )

        val gbrSchool = accountRepository.save(
            OrganisationFactory.school(
                name = "Independent GBR School",
                district = null,
                country = Country.fromCode(Country.GBR_ISO)
            )
        )

        val accounts =
            accountRepository.findAccounts(AccountSearchRequest(page = 0, size = 10, countryCode = Country.USA_ISO))

        assertThat(accounts).hasSize(3)
        assertThat(accounts).contains(district)
        assertThat(accounts).contains(districtSchool)
        assertThat(accounts).contains(usaSchool)
        assertThat(accounts).doesNotContain(gbrSchool)
    }
}
