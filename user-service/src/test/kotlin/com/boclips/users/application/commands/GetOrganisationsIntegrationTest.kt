package com.boclips.users.application.commands

import com.boclips.users.application.model.OrganisationFilter
import com.boclips.users.domain.model.organisation.OrganisationType
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.OrganisationDetailsFactory
import com.boclips.users.testsupport.factories.OrganisationFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class GetOrganisationsIntegrationTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var getOrganisations: GetOrganisations

    @Test
    fun `searching non usa organisations`() {

        val ukSchool = organisationRepository.save(
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.school(
                    name = "organisation 1",
                    country = Country.fromCode("GBR")
                )
            )
        )
        organisationRepository.save(
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.school(
                    name = "organisation 2",
                    country = Country.fromCode("USA"),
                    state = State.fromCode("NY")
                )
            )
        )

        organisationRepository.save(
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.district(
                    name = "another one",
                    state = State.fromCode("FL")
                )
            )
        )

        val searchRequest = OrganisationFilter(
            countryCode = "GBR",
            page = 0,
            size = 2
        )
        val organisations = getOrganisations(searchRequest)

        assertThat(organisations).hasSize(1)
        assertThat(organisations).containsExactly(ukSchool)
    }

    @Test
    fun `searching USA organisations`() {
        organisationRepository.save(
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.district(
                    name = "floridistrict",
                    state = State.fromCode("FL")
                )
            )
        )
        organisationRepository.save(
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.school(
                    name = "oregon-isation",
                    country = Country.fromCode("USA"),
                    state = State.fromCode("OR")
                )
            )
        )
        organisationRepository.save(
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.school(
                    name = "gb skool",
                    country = Country.fromCode("GBR")
                )
            )
        )

        val searchRequest = OrganisationFilter(
            countryCode = Country.USA_ISO,
            organisationTypes = listOf(OrganisationType.DISTRICT, OrganisationType.SCHOOL),
            page = 0,
            size = 2
        )
        val organisations = getOrganisations(searchRequest)

        assertThat(organisations).hasSize(2)
        assertThat(organisations.totalElements).isEqualTo(2)
        assertThat(organisations.items[0].details.name).isEqualTo("floridistrict")
        assertThat(organisations.items[0].details.country).isEqualTo(Country.usa())
        assertThat(organisations.items[1].details.name).isEqualTo("oregon-isation")
        assertThat(organisations.items[1].details.country).isEqualTo(Country.usa())
    }

    @Test
    fun `searching organisations without filters`() {
        organisationRepository.save(
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.district(
                    name = "floridistrict",
                    state = State.fromCode("FL")
                )
            )
        )
        organisationRepository.save(
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.school(
                    name = "oregon-isation",
                    country = Country.fromCode("USA"),
                    state = State.fromCode("OR")
                )
            )
        )
        organisationRepository.save(
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.school(
                    name = "gb skool",
                    country = Country.fromCode("GBR")
                )
            )
        )

        val searchRequest = OrganisationFilter(
            page = 0,
            size = 3
        )
        val organisations = getOrganisations(searchRequest)

        assertThat(organisations).hasSize(3)
        assertThat(organisations.totalElements).isEqualTo(3)
        assertThat(organisations.items[0].details.name).isEqualTo("floridistrict")
        assertThat(organisations.items[1].details.name).isEqualTo("gb skool")
        assertThat(organisations.items[2].details.name).isEqualTo("oregon-isation")
    }
}
