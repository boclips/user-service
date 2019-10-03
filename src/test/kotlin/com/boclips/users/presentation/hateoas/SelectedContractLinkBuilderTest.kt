package com.boclips.users.presentation.hateoas

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class SelectedContractLinkBuilderTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var selectedContractLinkBuilder: SelectedContractLinkBuilder

    @Test
    fun `add collection to contract link`() {
        val contractId = "contract-id"

        val link = selectedContractLinkBuilder.addCollection(contractId)

        assertThat(link.rel).isEqualTo("addCollection")
        assertThat(link.href).endsWith("/v1/selected-content-contracts/$contractId/collections/{collectionId}")
        assertThat(link.isTemplated).isTrue()
    }

    @Test
    fun `remove collection from contract link`() {
        val contractId = "contract-id"

        val link = selectedContractLinkBuilder.removeCollection(contractId)

        assertThat(link.rel).isEqualTo("removeCollection")
        assertThat(link.href).endsWith("/v1/selected-content-contracts/$contractId/collections/{collectionId}")
        assertThat(link.isTemplated).isTrue()
    }
}