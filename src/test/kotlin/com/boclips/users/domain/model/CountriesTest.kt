package com.boclips.users.domain.model

import com.boclips.users.domain.model.school.Country
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CountriesTest {
    @Test
    fun `list all 250 countries`() {
        assertThat(Countries.getAll()).hasSize(250)
    }

    @Test
    fun `can look up country`() {
        assertThat(Countries.getByCode("USA")).isEqualTo(Country(id = "USA", name = "United States"))
    }
}