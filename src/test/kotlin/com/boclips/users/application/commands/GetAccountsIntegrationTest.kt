package com.boclips.users.application.commands
import com.boclips.users.application.model.OrganisationFilter
import com.boclips.users.domain.model.account.OrganisationType
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.OrganisationFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class GetAccountsIntegrationTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var getAccounts: GetAccounts

    @Test
    fun `searching non usa organisations`() {
        val ukSchool = accountRepository.save(
            school = OrganisationFactory.school(
                name = "organisation 1",
                country = Country.fromCode("GBR")
            )
        )
        accountRepository.save(
            school = OrganisationFactory.school(
                name = "organisation 2",
                country = Country.fromCode("USA"),
                state = State.fromCode("NY")
            )
        )
        accountRepository.save(
            district = OrganisationFactory.district(
                name = "another one",
                state = State.fromCode("FL")
            )
        )

        val searchRequest = OrganisationFilter(
            countryCode = "GBR",
            page = 0,
            size = 2
        )
        val organisations = getAccounts(searchRequest)

        assertThat(organisations).hasSize(1)
        assertThat(organisations).containsExactly(ukSchool)
    }

    @Test
    fun `searching USA organisations`() {
        accountRepository.save(
            district = OrganisationFactory.district(
                name = "floridistrict",
                state = State.fromCode("FL")
            )
        )
        accountRepository.save(
            school = OrganisationFactory.school(
                name = "oregon-isation",
                country = Country.fromCode("USA"),
                state = State.fromCode("OR")
            )
        )
        accountRepository.save(
            school = OrganisationFactory.school(
                name = "gb skool",
                country = Country.fromCode("GBR")
            )
        )

        val searchRequest = OrganisationFilter(
            countryCode = Country.USA_ISO,
            organisationTypes = listOf(OrganisationType.DISTRICT, OrganisationType.SCHOOL),
            page = 0,
            size = 2
        )
        val organisations = getAccounts(searchRequest)

        assertThat(organisations).hasSize(2)
        assertThat(organisations.totalElements).isEqualTo(2)
        assertThat(organisations.content[0].organisation.name).isEqualTo("floridistrict")
        assertThat(organisations.content[0].organisation.country).isEqualTo(Country.usa())
        assertThat(organisations.content[1].organisation.name).isEqualTo("oregon-isation")
        assertThat(organisations.content[1].organisation.country).isEqualTo(Country.usa())
    }

    @Test
    fun `searching organisations without filters`() {
        accountRepository.save(
            district = OrganisationFactory.district(
                name = "floridistrict",
                state = State.fromCode("FL")
            )
        )
        accountRepository.save(
            school = OrganisationFactory.school(
                name = "oregon-isation",
                country = Country.fromCode("USA"),
                state = State.fromCode("OR")
            )
        )
        accountRepository.save(
            school = OrganisationFactory.school(
                name = "gb skool",
                country = Country.fromCode("GBR")
            )
        )

        val searchRequest = OrganisationFilter(
            page = 0,
            size = 3
        )
        val organisations = getAccounts(searchRequest)

        assertThat(organisations).hasSize(3)
        assertThat(organisations.totalElements).isEqualTo(3)
        assertThat(organisations.content[0].organisation.name).isEqualTo("floridistrict")
        assertThat(organisations.content[1].organisation.name).isEqualTo("gb skool")
        assertThat(organisations.content[2].organisation.name).isEqualTo("oregon-isation")
    }
}
