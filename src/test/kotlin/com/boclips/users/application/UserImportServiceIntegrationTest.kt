package com.boclips.users.application

import com.boclips.security.testing.setSecurityContext
import com.boclips.users.domain.model.UserId
import com.boclips.users.application.exceptions.UserNotFoundException
import com.boclips.users.infrastructure.keycloak.UserAlreadyExistsException
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

class UserImportServiceIntegrationTest : AbstractSpringIntegrationTest() {

    @Autowired
    lateinit var userImportService: UserImportService

    @Test
    fun `imports an identity`() {
        val userId = UserId(value = UUID.randomUUID().toString())

        setSecurityContext(userId.value)

        saveIdentity(UserFactory.sample(id = userId.value))

        userImportService.importFromIdentityProvider(userId = userId)

        val user = userRepository.findById(userId)

        assertThat(user!!.id).isEqualTo(userId)
    }

    @Test
    fun `throws when a user already exists`() {
        val user = UserFactory.sample(id = "some-id", firstName = "Bob")
        val savedUser = userRepository.save(user)
        setSecurityContext(savedUser.id.value)

        saveIdentity(UserFactory.sample(id = "some-id"))

        assertThrows<UserAlreadyExistsException> { userImportService.importFromIdentityProvider(userId = savedUser.id) }
    }

    @Test
    fun `import all identities with users`() {
        val userId1 = UUID.randomUUID().toString()
        val userId2 = UUID.randomUUID().toString()

        setSecurityContext(userId1)
        setSecurityContext(userId2)

        saveIdentity(UserFactory.sample(id = userId1))
        saveIdentity(UserFactory.sample(id = userId2))

        userImportService.importFromIdentityProvider(listOf(UserId(userId1), UserId(userId2)))

        val users = userRepository.findAll()

        assertThat(users.size).isEqualTo(2)
    }

    @Test
    fun `throws if identity does not exist`() {
        setSecurityContext("test-user")

        assertThrows<UserNotFoundException> { userImportService.importFromIdentityProvider(UserId(value = "test")) }
    }
}
