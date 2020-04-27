package com.boclips.users.application.commands

import com.boclips.users.application.model.OrganisationFilter
import com.boclips.users.domain.model.organisation.Address
import com.boclips.users.domain.model.organisation.ExternalOrganisationId
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
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
            OrganisationFactory.school(
                externalId = ExternalOrganisationId("org-id-1"),
                name = "organisation 1",
                address = Address(
                    country = Country.fromCode("GBR")
                )
            )
        )
        organisationRepository.save(
            OrganisationFactory.school(
                externalId = ExternalOrganisationId("org-id-2"),
                name = "organisation 2",
                address = Address(
                    country = Country.fromCode("USA"),
                    state = State.fromCode("NY")
                )
            )
        )
        organisationRepository.save(
            OrganisationFactory.district(
                externalId = ExternalOrganisationId("org-id-3"),
                name = "another one",
                address = Address(
                    state = State.fromCode("FL")
                )
            )
        )

        val organisations = getOrganisations(OrganisationFilter(countryCode = null, page = 0, size = 10))

        assertThat(organisations).hasSize(3)
        val organisationNames = organisations.map { it.name }
        assertThat(organisationNames).contains("organisation 1")
        assertThat(organisationNames).contains("organisation 2")
        assertThat(organisationNames).contains("another one")
    }
}
