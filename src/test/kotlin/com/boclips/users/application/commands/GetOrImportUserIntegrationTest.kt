package com.boclips.users.application.commands

import com.boclips.users.domain.model.Account
import com.boclips.users.domain.model.UserId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.UUID

class GetOrImportUserIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `imports the user if it doesn't exist and returns the created entry`() {
        val userId = UserId(UUID.randomUUID().toString())

        keycloakClientFake.createAccount(
            Account(
                id = userId,
                username = "service-account@somewhere.com"
            )
        )

        val importedUser = getOrImportUser(userId)
        val foundUser = userRepository.findById(userId)

        assertThat(importedUser).isEqualTo(foundUser)
    }

    @Test
    fun `returns an existing entry if it exists`() {
        val createdUser = userRepository.save(UserFactory.sample())

        val foundUser = userRepository.findById(createdUser.id)

        assertThat(foundUser).isEqualTo(createdUser)
    }
}
