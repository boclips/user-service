package com.boclips.users.domain.model.school

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CountryTest {
    @Test
    fun `can decode three letter country code`() {
        assertThat(Country.fromCode("HUN").name).isEqualTo("Hungary")
        assertThat(Country.fromCode("GUY").name).isEqualTo("Guyana")
        assertThat(Country.fromCode("GHA").name).isEqualTo("Ghana")
    }

    @Test
    fun `throws when country code invalid`() {
        Assertions.assertThatThrownBy { Country.fromCode("USB") }
            .hasMessage("Could not find three letter country code for USB")
    }
}