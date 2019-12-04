package com.boclips.users.application

import com.boclips.users.domain.model.UserId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.AccountFactory
import com.boclips.users.testsupport.factories.UserFactory
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.ZonedDateTime

class SynchronisationServiceIntegrationTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var synchronisationService: SynchronisationService

    @Test
    fun `updates CRM profiles including session information`() {
        saveUser(UserFactory.sample())

        synchronisationService.synchroniseCrmProfiles()

        verify(marketingService).updateProfile(any())
    }

    @Test
    fun `updates new accounts from account provider`() {
        val existingAccount = AccountFactory.sample(id = "cat", roles = listOf("ROLE_TEACHER"))
        keycloakClientFake.createAccount(existingAccount)
        saveUser(UserFactory.sample(id = existingAccount.id.value))

        keycloakClientFake.createAccount(AccountFactory.sample(id = "dog", roles = listOf("ROLE_TEACHER")))

        assertThat(userRepository.findAll()).hasSize(1)

        synchronisationService.synchroniseAccounts()

        assertThat(userRepository.findAll()).hasSize(2)
        assertThat(userRepository.findAll().map { it.id }).containsExactly(UserId("cat"), UserId("dog"))
    }
}
