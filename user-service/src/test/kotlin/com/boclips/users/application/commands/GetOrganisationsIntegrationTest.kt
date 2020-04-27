package com.boclips.users.application.commands

import com.boclips.users.application.model.OrganisationFilter
import com.boclips.users.domain.model.organisation.Address
import com.boclips.users.domain.model.organisation.OrganisationType
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
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
            OrganisationFactory.school(
                name = "organisation 1",
                address = Address(
                    country = Country.fromCode("GBR")
                )
            )
        )
        organisationRepository.save(
            OrganisationFactory.school(
                name = "organisation 2",
                address = Address(
                    country = Country.fromCode("USA"),
                    state = State.fromCode("NY")
                )
            )
        )

        organisationRepository.save(
            OrganisationFactory.district(
                name = "another one",
                address = Address(
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
            OrganisationFactory.district(
                name = "floridistrict",
                address = Address(
                    country = Country.fromCode("USA"),
                    state = State.fromCode("FL")
                )
            )
        )
        organisationRepository.save(
            OrganisationFactory.school(
                name = "oregon-isation",
                address = Address(
                    country = Country.fromCode("USA"),
                    state = State.fromCode("OR")
                )
            )
        )
        organisationRepository.save(
            OrganisationFactory.school(
                name = "gb skool",
                address = Address(
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
        assertThat(organisations.items[0].name).isEqualTo("floridistrict")
        assertThat(organisations.items[0].address.country).isEqualTo(Country.usa())
        assertThat(organisations.items[1].name).isEqualTo("oregon-isation")
        assertThat(organisations.items[1].address.country).isEqualTo(Country.usa())
    }

    @Test
    fun `searching organisations without filters`() {
        organisationRepository.save(
            OrganisationFactory.district(
                name = "floridistrict",
                address = Address(
                    state = State.fromCode("FL")
                )
            )
        )
        organisationRepository.save(
            OrganisationFactory.school(
                name = "oregon-isation",
                address = Address(
                    country = Country.fromCode("USA"),
                    state = State.fromCode("OR")
                )
            )
        )
        organisationRepository.save(
            OrganisationFactory.school(
                name = "gb skool",
                address = Address(
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
        assertThat(organisations.items[0].name).isEqualTo("floridistrict")
        assertThat(organisations.items[1].name).isEqualTo("gb skool")
        assertThat(organisations.items[2].name).isEqualTo("oregon-isation")
    }
}
