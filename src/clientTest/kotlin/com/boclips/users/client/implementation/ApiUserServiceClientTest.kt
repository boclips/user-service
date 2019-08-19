package com.boclips.users.client.implementation

import com.boclips.users.client.model.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ApiUserServiceClientTest {
    @Test
    fun `returns a user instance`() {
        assertThat(ApiUserServiceClient().findUser("whatever")).isInstanceOf(User::class.java)
    }
}
