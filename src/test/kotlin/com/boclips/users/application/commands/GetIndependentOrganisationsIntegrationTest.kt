package com.boclips.users.application.commands
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State
import com.boclips.users.infrastructure.organisation.OrganisationSearchRequest
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.OrganisationFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class GetIndependentOrganisationsIntegrationTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var getIndependentOrganisations: GetIndependentOrganisations

    @Test
    fun `searching non usa organisations`() {
        val ukSchool = organisationAccountRepository.save(
            school = OrganisationFactory.school(
                name = "organisation 1",
                country = Country.fromCode("GBR")
            )
        )
        organisationAccountRepository.save(
            school = OrganisationFactory.school(
                name = "organisation 2",
                country = Country.fromCode("USA"),
                state = State.fromCode("NY")
            )
        )
        organisationAccountRepository.save(
            district = OrganisationFactory.district(
                name = "another one",
                state = State.fromCode("FL")
            )
        )

        val searchRequest = OrganisationSearchRequest(
            countryCode = "GBR",
            page = 0,
            size = 2
        )
        val organisations = getIndependentOrganisations(searchRequest)

        assertThat(organisations).hasSize(1)
        assertThat(organisations).containsExactly(ukSchool)
    }

    @Test
    fun `searching USA organisations`() {
        organisationAccountRepository.save(
            district = OrganisationFactory.district(
                name = "floridistrict",
                state = State.fromCode("FL")
            )
        )
        organisationAccountRepository.save(
            school = OrganisationFactory.school(
                name = "oregon-isation",
                country = Country.fromCode("USA"),
                state = State.fromCode("OR")
            )
        )
        organisationAccountRepository.save(
            school = OrganisationFactory.school(
                name = "gb skool",
                country = Country.fromCode("GBR")
            )
        )

        val searchRequest = OrganisationSearchRequest(
            countryCode = Country.USA_ISO,
            page = 0,
            size = 2
        )
        val organisations = getIndependentOrganisations(searchRequest)

        assertThat(organisations).hasSize(2)
        assertThat(organisations.content[0].organisation.name).isEqualTo("floridistrict")
        assertThat(organisations.content[0].organisation.country).isEqualTo(Country.usa())
        assertThat(organisations.content[1].organisation.name).isEqualTo("oregon-isation")
        assertThat(organisations.content[1].organisation.country).isEqualTo(Country.usa())
    }

}
