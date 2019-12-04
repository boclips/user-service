package com.boclips.users.presentation.hateoas

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
    fun `expose organisations link`() {
        val organisationLink = organisationLinkBuilder.getSchoolsAndDistrictsLink(Country.USA_ISO)

        assertThat(organisationLink.rel).isEqualTo("independentOrganisations")
        assertThat(organisationLink.href).endsWith("/independent-organisations?countryCode=USA")
    }

    @Test
    fun `expose school link`() {
        val schoolLink = organisationLinkBuilder.getSchoolLink("USA")

        assertThat(schoolLink).isNotNull
        assertThat(schoolLink!!.href).endsWith("/schools?countryCode=USA{&query,state}")
    }
}
