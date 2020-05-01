package com.boclips.users.application.commands

import com.boclips.users.domain.model.user.Identity
import com.boclips.users.domain.model.user.UserId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime
import java.util.UUID

class GetOrImportUserIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `imports the user if it doesn't exist and returns the created entry`() {
        val userId = UserId(UUID.randomUUID().toString())

        keycloakClientFake.createAccount(
            Identity(
                id = userId,
                username = "service-account@somewhere.com",
                createdAt = ZonedDateTime.now()
            )
        )

        val importedUser = getOrImportUser(userId)
        val foundUser = userRepository.findById(userId)

        assertThat(importedUser).isEqualTo(foundUser)
    }

    @Test
    fun `returns an existing entry if it exists`() {
        val createdUser = userRepository.create(UserFactory.sample())

        val foundUser = userRepository.findById(createdUser.id)

        assertThat(foundUser).isEqualTo(createdUser)
    }
}
