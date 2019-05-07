package com.boclips.users.infrastructure.user

import com.boclips.users.domain.model.Subject
import com.boclips.users.domain.model.SubjectId
import com.boclips.users.domain.model.UserId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.AccountFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MongoUserRepositoryTest : AbstractSpringIntegrationTest() {

    @Test
    fun `save persists provided user`() {
        val user = AccountFactory.sample(
            subjects = listOf(Subject(id = SubjectId(value = "1"), name = "Maths"))
        )

        userRepository.save(user)

        assertThat(userRepository.findById(user.id)).isEqualTo(user)
    }

    @Test
    fun `saving analytics id as null is all good`() {
        val account = AccountFactory.sample(
            analyticsId = null,
            subjects = listOf(Subject(id = SubjectId(value = "1"), name = "Maths"))
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
            activated = false
        )

        userRepository.save(account)
        userRepository.activate(account.id)

        assertThat(userRepository.findById(account.id)!!.activated).isTrue()
    }

    @Test
    fun `count users`() {
        userRepository.save(AccountFactory.sample(
                id = "user-1",
                analyticsId = null,
                activated = false
        ))
        userRepository.save(AccountFactory.sample(
                id = "user=2",
                analyticsId = null,
                activated = false
        ))
        userRepository.activate(UserId("user-1"))

        val counts = userRepository.count()

        assertThat(counts.total).isEqualTo(2)
        assertThat(counts.activated).isEqualTo(1)
    }
}
