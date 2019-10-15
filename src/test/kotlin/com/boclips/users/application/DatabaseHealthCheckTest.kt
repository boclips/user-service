package com.boclips.users.application

import com.boclips.users.domain.service.UserRepository
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.actuate.health.Status

class DatabaseHealthCheckTest {
    @Test
    fun `is down when user retrieval throws`() {
        val repo = mock<UserRepository> {
            on { findById(any()) } doThrow RuntimeException("maybe I can't connect?")
        }

        val check = DatabaseHealthCheck(repo)

        assertThat(check.health().status).isEqualTo(Status("DOWN"))
    }

    @Test
    fun `is up when application can retrieve without exception`() {
        val repo = mock<UserRepository> {
            on { findById(any()) } doReturn null
        }

        val check = DatabaseHealthCheck(repo)

        assertThat(check.health().status).isEqualTo(Status("UP"))
    }
}
