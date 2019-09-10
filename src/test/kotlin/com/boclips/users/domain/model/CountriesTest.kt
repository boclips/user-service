package com.boclips.users.domain.model

import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CountriesTest {
    @Test
    fun `list all 250 countries`() {
        assertThat(Countries.getAll().size).isGreaterThanOrEqualTo(249)
    }

    @Test
    fun `can look up country`() {
        assertThat(Countries.getByCode("ESP")).isEqualTo(Country(id = "ESP", name = "Spain"))
    }

    @Test
    fun `includes states for USA`() {
        assertThat(Countries.getByCode("USA")).isEqualTo(Country(id = "USA", name = "United States", states = State.states()))
    }
}