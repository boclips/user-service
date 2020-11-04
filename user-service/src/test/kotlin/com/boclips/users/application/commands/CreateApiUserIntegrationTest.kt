package com.boclips.users.application.commands

import com.boclips.users.api.request.CreateApiUserRequest
import com.boclips.users.domain.model.user.UserId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.OrganisationFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class CreateApiUserIntegrationTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var createApiUser: CreateApiUser

    @Test
    fun `can create an api user given an organisation`() {
        val organisation = saveOrganisation(OrganisationFactory.apiIntegration())
        createApiUser(userId = "a-user-id", createApiUserRequest = CreateApiUserRequest(organisationId = organisation.id.value))

        assertThat(userRepository.findById(UserId("a-user-id"))).isNotNull
        assertThat(userRepository.findById(UserId("a-user-id"))?.organisation).isEqualTo(organisation)
    }
}