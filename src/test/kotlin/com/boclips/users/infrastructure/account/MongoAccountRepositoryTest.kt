package com.boclips.users.infrastructure.account

import com.boclips.users.testsupport.AbstractSpringIntergrationTest
import com.boclips.users.testsupport.AccountFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class MongoAccountRepositoryTest : AbstractSpringIntergrationTest() {

    @Autowired
    lateinit var userRepository: MongoAccountRepository

    @Test
    fun `save persists provided user`() {
        val user = AccountFactory.sample()

        userRepository.save(user)

        assertThat(userRepository.findById(user.id)).isEqualTo(user)
    }

    @Test
    fun `saving null fields is all good`() {
        val user = AccountFactory.sample(
            analyticsId = null,
            subjects = null
        )

        userRepository.save(user)

        assertThat(userRepository.findById(user.id)).isEqualTo(user)
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
}