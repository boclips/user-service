package com.boclips.users.presentation.hateoas

import com.boclips.users.domain.model.contract.ContractId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ContractsLinkBuilderTest {
    @Test
    fun `creates a self link to contract`() {
        val contractIdValue = "test-contract-id"

        val selfLink = builder.self(ContractId(contractIdValue))

        assertThat(selfLink.rel).isEqualTo("self")
        assertThat(selfLink.href).endsWith("/v1/contracts/$contractIdValue")
    }

    private val builder = ContractsLinkBuilder()
}
