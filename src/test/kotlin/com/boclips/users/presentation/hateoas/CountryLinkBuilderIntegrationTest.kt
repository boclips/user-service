package com.boclips.users.presentation.hateoas

import com.boclips.security.testing.setSecurityContext
import com.boclips.users.domain.model.school.Country
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class CountryLinkBuilderIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `when authenticated expose link to list of all countries`() {
        setSecurityContext("some-user")

        val countriesLink = countryLinkBuilder.getCountriesLink()

        Assertions.assertThat(countriesLink).isNotNull()
        Assertions.assertThat(countriesLink!!.href).endsWith("/countries")
        Assertions.assertThat(countriesLink.rel).isEqualTo("countries")
    }

    @Test
    fun `when not authenticated we do not expose list of all countries`() {
        val countriesLink = countryLinkBuilder.getCountriesLink()

        Assertions.assertThat(countriesLink).isNull()
    }

    @Test
    fun `self link for countries`() {
        val selfLink = countryLinkBuilder.getCountriesSelfLink()

        Assertions.assertThat(selfLink).isNotNull()
        Assertions.assertThat(selfLink!!.href).endsWith("/countries")
        Assertions.assertThat(selfLink.rel).isEqualTo("self")
    }

    @Test
    fun `expose state link when country is the USA`() {
        val country = Country.fromCode("USA")
        val stateLink = countryLinkBuilder.getStatesLink(country)

        Assertions.assertThat(stateLink).isNotNull()
        Assertions.assertThat(stateLink!!.href).endsWith("countries/USA/states")
        Assertions.assertThat(stateLink.rel).isEqualTo("states")
    }

    @Test
    fun `does not expose state link when country is not the USA`() {
        val country = Country.fromCode("GBR")
        val stateLink = countryLinkBuilder.getStatesLink(country)

        Assertions.assertThat(stateLink).isNull()
    }

    val countryLinkBuilder = CountryLinkBuilder()
}