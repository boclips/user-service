package com.boclips.users.api.request.user

import org.assertj.core.api.AssertionsForInterfaceTypes
import org.junit.jupiter.api.Test

internal class RoleValidatorTest {
    @Test
    fun `valid role`() {
        AssertionsForInterfaceTypes.assertThat(RoleValidator().isValid("TEACHER", null)).isTrue()
    }
    @Test
    fun `invalid role`() {
        AssertionsForInterfaceTypes.assertThat(RoleValidator().isValid("NONTEACHER", null)).isFalse()
    }
}