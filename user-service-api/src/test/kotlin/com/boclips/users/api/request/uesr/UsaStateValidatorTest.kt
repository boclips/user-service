package com.boclips.users.api.request.uesr

import com.boclips.users.api.request.user.UsaStateValidator
import org.assertj.core.api.AssertionsForInterfaceTypes
import org.junit.jupiter.api.Test

class UsaStateValidatorTest {
    @Test
    fun `valid state`() {
        AssertionsForInterfaceTypes.assertThat(UsaStateValidator().isValid("CA", null)).isTrue()
    }

    @Test
    fun `null state`() {
        AssertionsForInterfaceTypes.assertThat(UsaStateValidator().isValid(null, null)).isTrue()
    }

    @Test
    fun `invalid state`() {
        AssertionsForInterfaceTypes.assertThat(UsaStateValidator().isValid("XX", null)).isFalse()
    }
}
