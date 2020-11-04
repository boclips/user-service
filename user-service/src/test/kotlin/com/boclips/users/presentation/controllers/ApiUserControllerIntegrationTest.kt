package com.boclips.users.presentation.controllers

import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.user.UserId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.asUserWithRoles
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

class ApiUserControllerIntegrationTest : AbstractSpringIntegrationTest() {

    @Test
    fun `can create an api user with given organisation`() {
        val organisation = saveOrganisation(OrganisationFactory.apiIntegration())

        mvc.perform(
            MockMvcRequestBuilders.put("/v1/api-users/1")
                .asUserWithRoles(id = "service-account-gateway", roles = *arrayOf(UserRoles.CREATE_API_USERS))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                     "organisationId": "${organisation.id.value}"
                     }
                    """.trimIndent()
                )
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)

        assertThat(userRepository.findById(UserId("1"))).isNotNull
        assertThat(userRepository.findById(UserId("1"))?.organisation).isEqualTo(organisation)
    }

    @Test
    fun `returns no content when putting a user that already exists`() {
        val organisation = saveOrganisation(OrganisationFactory.apiIntegration())
        saveUser(UserFactory.sample("1"))

        mvc.perform(
            MockMvcRequestBuilders.put("/v1/api-users/1")
                .asUserWithRoles(id = "service-account-gateway", roles = *arrayOf(UserRoles.CREATE_API_USERS))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                     "organisationId": "${organisation.id.value}"
                     }
                    """.trimIndent()
                )
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent)
    }
}
