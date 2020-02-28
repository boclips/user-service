package com.boclips.users.presentation.hateoas

import com.boclips.security.testing.setSecurityContext
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.organisation.OrganisationId
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.util.UriComponentsBuilder

internal class OrganisationLinkBuilderTest {

    lateinit var accountLinkBuilder: AccountLinkBuilder
    lateinit var uriComponentsBuilderFactory :UriComponentsBuilderFactory

    @BeforeEach
    fun setUp() {
        uriComponentsBuilderFactory = mock()
        whenever(uriComponentsBuilderFactory.getInstance()).thenReturn(UriComponentsBuilder.newInstance())
        accountLinkBuilder = AccountLinkBuilder(uriComponentsBuilderFactory = uriComponentsBuilderFactory)
    }

    @Test
    fun `edit link for organisation`() {
        val organisationId = "test-id"
        val organisationLink = accountLinkBuilder.edit(OrganisationId(organisationId))

        assertThat(organisationLink.rel.value()).isEqualTo("edit")
        assertThat(organisationLink.href).endsWith("/accounts/$organisationId")
    }

    @Test
    fun `expose organisations link`() {
        setSecurityContext("org-viewer", UserRoles.VIEW_ORGANISATIONS)
        val organisationLink = accountLinkBuilder.getIndependentAccountsLink()

        assertThat(organisationLink!!.rel.value()).isEqualTo("independentAccounts")
        assertThat(organisationLink.href).endsWith("/accounts{?countryCode,page,size}")
    }

    @Test
    fun `returns a next link, when there are more pages`() {
        val currentPage = 0
        val totalPages = 4

        whenever(uriComponentsBuilderFactory.getInstance()).thenReturn(UriComponentsBuilder.fromHttpUrl("https://localhost/v1?page=${currentPage}"))
        val nextLink = accountLinkBuilder.getNextPageLink(currentPage, totalPages)

        assertThat(nextLink).isNotNull
        assertThat(nextLink!!.href).contains("page=1")
        assertThat(nextLink.href).doesNotContain("page=0")
        assertThat(nextLink.rel.value()).contains("next")
    }

    @Test fun `does not return a next link when there are no more pages`() {
        val currentPage = 2
        val totalPages = 3

        assertThat(accountLinkBuilder.getNextPageLink(currentPage, totalPages)).isNull()
    }
}
