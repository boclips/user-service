package com.boclips.users.infrastructure.user

import com.boclips.users.testsupport.AbstractSpringIntergrationTest
import com.boclips.users.testsupport.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class MongoUserRepositoryTest : AbstractSpringIntergrationTest() {

    @Autowired
    lateinit var userRepository: MongoUserRepository

    @Test
    fun `save persists provided user`() {
        val user = UserFactory.sample()

        userRepository.save(user)

        val userAfterSave = userRepository.findById(user.id)
        assertThat(userAfterSave?.id).isEqualTo(user.id)
        assertThat(userAfterSave?.activated).isEqualTo(user.activated)
    }
}