package com.boclips.users.application

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
    fun `updates new identities from identity provider`() {
        val existingIdentity = IdentityFactory.sample()
        keycloakClientFake.createUser(existingIdentity)
        keycloakClientFake.createUser(IdentityFactory.sample())
        saveUser(UserFactory.sample(id = existingIdentity.id.value))

        synchronisationService.synchroniseIdentities()

        assertThat(userRepository.findAll()).hasSize(2)
    }
}
