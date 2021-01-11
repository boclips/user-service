package com.boclips.users.application.commands

import com.boclips.users.api.request.user.CreateUserRequest
import com.boclips.users.application.exceptions.ApiUserAlreadyExistsException
import com.boclips.users.domain.model.user.UserId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

class CreateApiUserIntegrationTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var createApiUser: CreateApiUser

    @Test
    fun `can create an api user given an organisation`() {
        val organisation = saveOrganisation(OrganisationFactory.apiIntegration())
        createApiUser(
            createApiUserRequest = CreateUserRequest.CreateApiUserRequest(
                apiUserId = "a-user-id",
                organisationId = organisation.id.value
            )
        )

        assertThat(userRepository.findById(UserId("a-user-id"))).isNotNull
        assertThat(userRepository.findById(UserId("a-user-id"))?.organisation).isEqualTo(organisation)
    }

    @Test
    fun `throws when trying to create a user that already exists`() {
        val organisation = saveOrganisation(OrganisationFactory.apiIntegration())
        saveUser(UserFactory.sample("a-user-id"))

        assertThrows<ApiUserAlreadyExistsException> {
            createApiUser(
                createApiUserRequest = CreateUserRequest.CreateApiUserRequest(
                    apiUserId = "a-user-id",
                    organisationId = organisation.id.value
                )
            )
        }
    }
}
