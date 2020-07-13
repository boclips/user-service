package com.boclips.users.presentation.controllers

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.asBoclipsService
import com.boclips.users.testsupport.asOperator
import com.boclips.users.testsupport.factories.OrganisationFactory
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class UserTestSupportControllerIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `can create a user assigned to an organisation`() {
        val organisation = saveOrganisation(OrganisationFactory.apiIntegration())

        mvc.perform(
            MockMvcRequestBuilders.post("/v1/e2e-users").asOperator()
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                     "email": "jane@doe.com",
                     "password": "Champagn3",
                     "organisationId": "${organisation.id.value}"
                     }
                    """.trimIndent()
                )
        )
            .andExpect(status().isCreated)
            .andExpect(header().exists("Location"))
            .andDo { result ->
                mvc.perform(
                    MockMvcRequestBuilders.get(result.response.getHeaderValue("Location") as String).asBoclipsService()
                ).andExpect(status().isOk)
                    .andExpect(jsonPath("$.email", equalTo("jane@doe.com")))
                    .andExpect(jsonPath("$.organisation.id", equalTo(organisation.id.value)))
            }
    }

    @Test
    fun `returns 404 for bad organisation id`() {
        mvc.perform(
            MockMvcRequestBuilders.post("/v1/e2e-users").asOperator()
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                     "email": "jane@doe.com",
                     "password": "Champagn3",
                     "organisationId": "missing"
                     }
                    """.trimIndent()
                )
        )
            .andExpect(status().isNotFound)
    }
}
