package com.boclips.users.presentation.hateoas

import com.boclips.users.domain.model.organisation.OrganisationAccountId
import com.boclips.users.domain.model.school.Country
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.util.UriComponentsBuilder

internal class OrganisationLinkBuilderTest {

    lateinit var organisationLinkBuilder: OrganisationLinkBuilder
    lateinit var uriComponentsBuilderFactory :UriComponentsBuilderFactory

    @BeforeEach
    fun setUp() {
        uriComponentsBuilderFactory = mock()
        whenever(uriComponentsBuilderFactory.getInstance()).thenReturn(UriComponentsBuilder.newInstance())
        organisationLinkBuilder = OrganisationLinkBuilder(uriComponentsBuilderFactory = uriComponentsBuilderFactory)
    }

    @Test
    fun `self link for organisation`() {
        val organisationId = "test-id"
        val organisationLink = organisationLinkBuilder.self(OrganisationAccountId(organisationId))

        assertThat(organisationLink.rel).isEqualTo("self")
        assertThat(organisationLink.href).endsWith("/organisations/$organisationId")
    }

    @Test
    fun `edit link for organisation`() {
        val organisationId = "test-id"
        val organisationLink = organisationLinkBuilder.edit(OrganisationAccountId(organisationId))

        assertThat(organisationLink.rel).isEqualTo("edit")
        assertThat(organisationLink.href).endsWith("/organisations/$organisationId")
    }

    @Test
    fun `expose organisations link`() {
        val organisationLink = organisationLinkBuilder.getIndependentOrganisationsLink(Country.USA_ISO)

        assertThat(organisationLink.rel).isEqualTo("independentOrganisations")
        assertThat(organisationLink.href).endsWith("/independent-organisations?countryCode=USA&page=0&size=30")
    }

    @Test
    fun `returns a next link, when there are more pages`() {
        val currentPage = 0
        val totalPages = 4

        whenever(uriComponentsBuilderFactory.getInstance()).thenReturn(UriComponentsBuilder.fromHttpUrl("https://localhost/v1?page=${currentPage}"))
        val nextLink = organisationLinkBuilder.getNextPageLink(currentPage, totalPages)

        assertThat(nextLink).isNotNull
        assertThat(nextLink!!.href).contains("page=1")
        assertThat(nextLink.href).doesNotContain("page=0")
        assertThat(nextLink.rel).contains("next")
    }

    @Test fun `does not return a next link when there are no more pages`() {
        val currentPage = 2
        val totalPages = 3

        assertThat(organisationLinkBuilder.getNextPageLink(currentPage, totalPages)).isNull()
    }

    @Test
    fun `expose school link`() {
        val schoolLink = organisationLinkBuilder.getSchoolLink("USA")

        assertThat(schoolLink).isNotNull
        assertThat(schoolLink!!.href).endsWith("/schools?countryCode=USA{&query,state}")
    }
}
