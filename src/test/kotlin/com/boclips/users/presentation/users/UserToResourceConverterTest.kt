package com.boclips.users.presentation.users

import com.boclips.users.presentation.users.UserToResourceConverter
import com.boclips.users.testsupport.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UserToResourceConverterTest {
    @Test
    fun `converts a user to a UserResource`() {
        val user = UserFactory.sample(
                id = "test",
                activated = false
        )
        val userResource = UserToResourceConverter.convert(user)

        assertThat(userResource.id).isEqualTo("test")
        assertThat(userResource.activated).isEqualTo(false)
    }
}