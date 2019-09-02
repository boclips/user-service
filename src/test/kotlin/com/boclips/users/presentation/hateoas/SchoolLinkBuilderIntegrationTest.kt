package com.boclips.users.presentation.hateoas

import com.boclips.security.testing.setSecurityContext
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class SchoolLinkBuilderIntegrationTest : AbstractSpringIntegrationTest() {

    @Autowired
    lateinit var schoolUriBuilder: SchoolLinkBuilder

    @Test
    fun `when authenticated expose link to list of all countries`() {
        setSecurityContext("some-user")

        val countriesLink = schoolUriBuilder.getCountriesLink()

        assertThat(countriesLink).isNotNull()
        assertThat(countriesLink!!.href).endsWith("/countries")
        assertThat(countriesLink.rel).isEqualTo("countries")
    }

    @Test
    fun `when not authenticated we do not expose list of all countries`() {
        val countriesLink = schoolUriBuilder.getCountriesLink()

        assertThat(countriesLink).isNull()
    }

    @Test
    fun `self link for countries`() {
        val selfLink = schoolUriBuilder.getCountriesSelfLink()

        assertThat(selfLink).isNotNull()
        assertThat(selfLink!!.href).endsWith("/countries")
        assertThat(selfLink.rel).isEqualTo("self")
    }
}