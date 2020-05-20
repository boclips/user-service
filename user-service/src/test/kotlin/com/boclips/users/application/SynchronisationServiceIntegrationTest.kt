package com.boclips.users.application

import com.boclips.users.domain.model.user.UserId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.IdentityFactory
import com.boclips.users.testsupport.factories.UserFactory
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

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
        val existingIdentity = IdentityFactory.sample(id = "cat", roles = listOf("ROLE_TEACHER"))
        keycloakClientFake.createAccount(existingIdentity)
        saveUser(UserFactory.sample(id = existingIdentity.id.value))

        keycloakClientFake.createAccount(IdentityFactory.sample(id = "dog", roles = listOf("ROLE_TEACHER")))

        assertThat(userRepository.findAll()).hasSize(1)

        synchronisationService.synchroniseUserAccounts()

        assertThat(userRepository.findAll()).hasSize(2)
        assertThat(userRepository.findAll().map { it.id }).containsExactly(
            UserId(
                "cat"
            ), UserId("dog")
        )
    }

    @Test
    fun `migrates MOE users with emails for firstnames to have an email instead`() {
        val existingIdentity = IdentityFactory.sample(
            id = "moeUser",
            roles = listOf("ROLE_TEACHER", "ROLE_MOE_UAE"),
            firstName = "first@name.com",
            username = "1234",
            idpEmail = ""
        )
        val nonMoeIdentity = IdentityFactory.sample(
            id = "nonMoeUser",
            firstName = "first@name.com",
            roles = listOf("ROLE_TEACHER"),
            username = "second@name.com"
        )

        keycloakClientFake.createAccount(existingIdentity)
        keycloakClientFake.createAccount(nonMoeIdentity)
        userRepository.create(UserFactory.sample(identity = existingIdentity))
        userRepository.create(UserFactory.sample(identity = nonMoeIdentity))

        assertThat(userRepository.findAll().map { it.identity.email }).containsExactly(
            null,"second@name.com"
        )
        synchronisationService.synchroniseMoeAccountEmails()

        assertThat(userRepository.findAll()).hasSize(2)
        assertThat(userRepository.findAll().map { it.identity.email }).containsExactly(
            "first@name.com","second@name.com"
        )
    }
}
