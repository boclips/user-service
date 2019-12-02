package com.boclips.users.application.commands
import com.boclips.users.domain.model.LookupEntry
import com.boclips.users.domain.model.organisation.School
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.OrganisationFactory
import org.assertj.core.api.Assertions.assertThat
import org.bouncycastle.asn1.x500.style.RFC4519Style.name
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class SearchOrganisationsIntegrationTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var searchOrganisations: SearchOrganisations

    @Test
    fun `searchiong non usa organisations`() {
        organisationAccountRepository.save(
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

        val organisations = searchOrganisations(organisationName = null, countryCode = "GBR", state = null)

        assertThat(organisations).hasSize(1)
        assertThat(organisations[0].organisation.name).isEqualTo("organisation 1")
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

        val organisations = searchOrganisations(organisationName = null, countryCode = "USA", state = null)

        assertThat(organisations).hasSize(2)
        assertThat(organisations[0].organisation.name).isEqualTo("floridistrict")
        assertThat(organisations[0].organisation.country).isEqualTo(Country.usa())
        assertThat(organisations[1].organisation.name).isEqualTo("oregon-isation")
        assertThat(organisations[1].organisation.country).isEqualTo(Country.usa())
    }

}
