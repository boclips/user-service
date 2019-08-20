package com.boclips.users.application

import com.boclips.users.presentation.requests.UpdateUserRequest
import com.boclips.users.testsupport.factories.UpdateUserRequestFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired

class UserUpdatesConverterTest {
    @Test
    fun `converts first name change to a command`() {
        val commands = UserUpdatesConverter().convert(UpdateUserRequest(firstName = "Rebecca"))

        assertThat(commands).hasSize(1)
    }

    @Test
    fun `converts last name change to a command`() {
        val commands = UserUpdatesConverter().convert(UpdateUserRequest(lastName = "Rebecca"))

        assertThat(commands).hasSize(1)
    }

    @Test
    fun `converts subjects change to a command`() {
        val commands = UserUpdatesConverter().convert(UpdateUserRequest(subjects = listOf("Maths")))

        assertThat(commands).hasSize(1)
    }

    @Test
    fun `converts ages change to a command`() {
        val commands = UserUpdatesConverter().convert(UpdateUserRequest(ages = listOf(9, 10, 11)))

        assertThat(commands).hasSize(1)
    }

    @Test
    fun `converts marketing opt in change to a command`() {
        val commands = UserUpdatesConverter().convert(UpdateUserRequest(hasOptedIntoMarketing = true))

        assertThat(commands).hasSize(1)
    }
}