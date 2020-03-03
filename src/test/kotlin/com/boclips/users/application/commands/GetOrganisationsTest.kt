package com.boclips.users.application.commands

import com.boclips.users.application.model.OrganisationFilter
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.OrganisationDetailsFactory
import com.boclips.users.testsupport.factories.OrganisationFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class GetOrganisationsTest : AbstractSpringIntegrationTest() {

    @Autowired
    lateinit var getOrganisations: GetOrganisations

    @Test
    fun `Lists all organisations`() {
        organisationRepository.save(
            OrganisationFactory.sample(
                organisation = OrganisationDetailsFactory.school(
                    externalId = "org-id-1",
                    name = "organisation 1",
                    country = Country.fromCode("GBR")
                )
            )
        )
        organisationRepository.save(
            OrganisationFactory.sample(
                organisation = OrganisationDetailsFactory.school(
                    externalId = "org-id-2",
                    name = "organisation 2",
                    country = Country.fromCode("USA"),
                    state = State.fromCode("NY")
                )
            )
        )
        organisationRepository.save(
            OrganisationFactory.sample(
                organisation = OrganisationDetailsFactory.district(
                    externalId = "org-id-3",
                    name = "another one",
                    state = State.fromCode("FL")
                )
            )
        )

        val organisations = getOrganisations(OrganisationFilter(countryCode = null, page = 0, size = 10))

        assertThat(organisations).hasSize(3)
        val organisationNames = organisations.content.map { it.organisation.name }
        assertThat(organisationNames).contains("organisation 1")
        assertThat(organisationNames).contains("organisation 2")
        assertThat(organisationNames).contains("another one")
    }
}
