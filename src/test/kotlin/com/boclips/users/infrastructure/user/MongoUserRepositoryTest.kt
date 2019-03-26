package com.boclips.users.infrastructure.user

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.AccountFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class
MongoUserRepositoryTest : AbstractSpringIntegrationTest() {

    @Test
    fun `save persists provided user`() {
        val user = AccountFactory.sample()

        userRepository.save(user)

        assertThat(userRepository.findById(user.id)).isEqualTo(user)
    }

    @Test
    fun `saving null fields is all good`() {
        val account = AccountFactory.sample(
            analyticsId = null,
            subjects = null
        )

        userRepository.save(account)

        assertThat(userRepository.findById(account.id)).isEqualTo(account)
    }

    @Test
    fun `can get all accounts`() {
        val savedUsers = listOf(
            userRepository.save(AccountFactory.sample()),
            userRepository.save(AccountFactory.sample()),
            userRepository.save(AccountFactory.sample()),
            userRepository.save(AccountFactory.sample()),
            userRepository.save(AccountFactory.sample()),
            userRepository.save(AccountFactory.sample())
        )

        assertThat(userRepository.findAll(savedUsers.map { it.id })).containsAll(savedUsers)
    }

    @Test
    fun `activate an account`() {
        val account = AccountFactory.sample(
            analyticsId = null,
            subjects = null,
            activated = false
        )

        userRepository.save(account)
        userRepository.activate(account.id)

        assertThat(userRepository.findById(account.id)!!.activated).isTrue()
    }
}