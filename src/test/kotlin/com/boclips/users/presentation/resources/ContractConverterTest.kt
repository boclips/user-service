package com.boclips.users.presentation.resources

import com.boclips.users.domain.model.contract.ContractId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ContractConverterTest {
    @Test
    fun `converts from contract id`() {
        assertThat(converter.convert(ContractId("A"))).isEqualTo(ContractResource("A"))
    }

    private val converter = ContractConverter()
}