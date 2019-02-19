package com.boclips.users.infrastructure.user

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
}