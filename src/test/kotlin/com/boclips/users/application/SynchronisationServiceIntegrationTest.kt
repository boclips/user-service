package com.boclips.users.application

import com.boclips.security.testing.setSecurityContext
import com.boclips.users.domain.model.UserId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.AccountFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

class SynchronisationServiceIntegrationTest : AbstractSpringIntegrationTest() {

    @Autowired
    lateinit var synchronisationService: SynchronisationService

    @Test
    fun `synchronise an identity`() {
        val userId = UserId(value = UUID.randomUUID().toString())

        setSecurityContext(userId.value)

        saveIdentity(AccountFactory.sample(id = userId.value))

        synchronisationService.synchronise(userId = userId)

        val user = userRepository.findById(userId)

        assertThat(user!!.id).isEqualTo(userId)
    }

    @Test
    fun `cannot synchronise existing user`() {
        val userId = UserId(value = UUID.randomUUID().toString())

        setSecurityContext(userId.value)

        saveIdentity(AccountFactory.sample(id = userId.value))

        synchronisationService.synchronise(userId = userId)
        synchronisationService.synchronise(userId = userId)

        assertThat(userRepository.findAll()).hasSize(1)
    }

    @Test
    fun `synchronise all identities with users`() {
        val userId1 = UUID.randomUUID().toString()
        val userId2 = UUID.randomUUID().toString()

        setSecurityContext(userId1)
        setSecurityContext(userId2)

        saveIdentity(AccountFactory.sample(id = userId1))
        saveIdentity(AccountFactory.sample(id = userId2))

        synchronisationService.synchroniseAll()

        val users = userRepository.findAll()

        assertThat(users.size).isEqualTo(2)
    }
}
