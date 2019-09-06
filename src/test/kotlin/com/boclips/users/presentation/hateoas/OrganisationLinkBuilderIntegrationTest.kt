package com.boclips.users.presentation.hateoas

import com.boclips.security.testing.setSecurityContext
import com.boclips.users.domain.model.organisation.OrganisationAccountId
import com.boclips.users.domain.model.school.Country
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class OrganisationLinkBuilderIntegrationTest : AbstractSpringIntegrationTest() {

    @Autowired
    lateinit var organisationLinkBuilder: OrganisationLinkBuilder

    @Test
    fun `self link for organisation`() {
        val organisationId = "test-id"
        val organisationLink = organisationLinkBuilder.self(OrganisationAccountId(organisationId))

        assertThat(organisationLink.rel).isEqualTo("self")
        assertThat(organisationLink.href).endsWith("/organisations/$organisationId")
    }

    @Test
    fun `when authenticated expose link to list of all countries`() {
        setSecurityContext("some-user")

        val countriesLink = organisationLinkBuilder.getCountriesLink()

        assertThat(countriesLink).isNotNull()
        assertThat(countriesLink!!.href).endsWith("/countries")
        assertThat(countriesLink.rel).isEqualTo("countries")
    }

    @Test
    fun `when not authenticated we do not expose list of all countries`() {
        val countriesLink = organisationLinkBuilder.getCountriesLink()

        assertThat(countriesLink).isNull()
    }

    @Test
    fun `self link for countries`() {
        val selfLink = organisationLinkBuilder.getCountriesSelfLink()

        assertThat(selfLink).isNotNull()
        assertThat(selfLink!!.href).endsWith("/countries")
        assertThat(selfLink.rel).isEqualTo("self")
    }

    @Test
    fun `expose state link when country is the USA`() {
        val country = Country.fromCode("USA")
        val stateLink = organisationLinkBuilder.getStatesLink(country)

        assertThat(stateLink).isNotNull()
        assertThat(stateLink!!.href).endsWith("countries/USA/states")
        assertThat(stateLink.rel).isEqualTo("states")
    }

    @Test
    fun `does not expose state link when country is not the USA`() {
        val country = Country.fromCode("GBR")
        val stateLink = organisationLinkBuilder.getStatesLink(country)

        assertThat(stateLink).isNull()
    }

    @Test
    fun `expose school link`() {
        val schoolLink = organisationLinkBuilder.getSchoolLink("USA")

        assertThat(schoolLink).isNotNull
        assertThat(schoolLink!!.href).endsWith("/schools?countryCode=USA{&query,state}")
    }
}