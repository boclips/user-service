package com.boclips.users.infrastructure.account

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.AccountFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MongoAccountRepositoryTest : AbstractSpringIntegrationTest() {

    @Test
    fun `save persists provided user`() {
        val user = AccountFactory.sample()

        accountRepository.save(user)

        assertThat(accountRepository.findById(user.id)).isEqualTo(user)
    }

    @Test
    fun `saving null fields is all good`() {
        val account = AccountFactory.sample(
            analyticsId = null,
            subjects = null
        )

        accountRepository.save(account)

        assertThat(accountRepository.findById(account.id)).isEqualTo(account)
    }

    @Test
    fun `can get all accounts`() {
        val savedUsers = listOf(
            accountRepository.save(AccountFactory.sample()),
            accountRepository.save(AccountFactory.sample()),
            accountRepository.save(AccountFactory.sample()),
            accountRepository.save(AccountFactory.sample()),
            accountRepository.save(AccountFactory.sample()),
            accountRepository.save(AccountFactory.sample())
        )

        assertThat(accountRepository.findAll(savedUsers.map { it.id })).containsAll(savedUsers)
    }

    @Test
    fun `activate an account`() {
        val account = AccountFactory.sample(
            analyticsId = null,
            subjects = null,
            activated = false
        )

        accountRepository.save(account)
        accountRepository.activate(account.id)

        assertThat(accountRepository.findById(account.id)!!.activated).isTrue()
    }

    @Test
    fun `mark account as referred`() {
        val account = AccountFactory.sample(
            analyticsId = null,
            subjects = null,
            isReferral = false
        )

        accountRepository.save(account)
        accountRepository.markAsReferred(account.id)

        assertThat(accountRepository.findById(account.id)!!.isReferral).isTrue()
    }
}