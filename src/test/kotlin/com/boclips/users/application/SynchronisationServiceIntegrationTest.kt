package com.boclips.users.application

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.UserFactory
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class SynchronisationServiceIntegrationTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var synchronisationService: SynchronisationService

    @Test
    fun `updates contacts including session information`() {
        saveUser(UserFactory.sample())

        synchronisationService.synchroniseTeachers()

        verify(marketingService).updateProfile(any())
    }
}
