package com.boclips.users.presentation.hateoas

import com.boclips.security.testing.setSecurityContext
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ContractLinkBuilderTest : AbstractSpringIntegrationTest() {
    @Test
    fun `creates a self link to contract`() {
        val contractIdValue = "test-contract-id"

        val selfLink = contractLinkBuilder.self(ContractId(contractIdValue))

        assertThat(selfLink.rel).isEqualTo("self")
        assertThat(selfLink.href).endsWith("/v1/contracts/$contractIdValue")
    }

    @Nested
    inner class SearchContracts {
        @BeforeEach
        fun setupSecurityContext() {
            setSecurityContext("role-abiding-citizen", UserRoles.VIEW_CONTRACTS)
        }

        @Test
        fun `returns a templated link if name is not provided`() {
            val searchContractsLink = contractLinkBuilder.searchContracts()

            assertThat(searchContractsLink!!.rel).isEqualTo("searchContracts")
            assertThat(searchContractsLink.isTemplated).isTrue()
            assertThat(searchContractsLink.href).endsWith("/v1/contracts{?name}")
        }

        @Test
        fun `returns a fixed link when name is provided`() {
            val searchContractsLink = contractLinkBuilder.searchContracts(name = "Hello world")

            assertThat(searchContractsLink!!.rel).isEqualTo("searchContracts")
            assertThat(searchContractsLink.isTemplated).isFalse()
            assertThat(searchContractsLink.href).endsWith("/v1/contracts?name=Hello%20world")
        }

        @Test
        fun `returns null if user does not have VIEW_CONTRACTS role`() {
            setSecurityContext("rebel-without-a-role")

            val searchContractsLink = contractLinkBuilder.searchContracts()

            assertThat(searchContractsLink).isNull()
        }

        @Test
        fun `allows to override rel`() {
            val searchContractsLink = contractLinkBuilder.searchContracts(rel = "self")

            assertThat(searchContractsLink!!.rel).isEqualTo("self")
            assertThat(searchContractsLink.isTemplated).isTrue()
            assertThat(searchContractsLink.href).endsWith("/v1/contracts{?name}")
        }
    }
}
