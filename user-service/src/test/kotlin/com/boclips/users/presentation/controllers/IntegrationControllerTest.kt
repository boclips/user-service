package com.boclips.users.presentation.controllers

import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.organisation.LtiDeployment
import com.boclips.users.domain.model.organisation.OrganisationType
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.asUserWithRoles
import com.boclips.users.testsupport.factories.IdentityFactory
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath

class IntegrationControllerTest : AbstractSpringIntegrationTest() {

    @Test
    fun `creates user and deploymentOrganisation if didn't exist and returns the internal userId`() {
        val baseLtiOrganisation = saveOrganisation(OrganisationFactory.apiIntegration(
            name = "lti-integration-organisation"
        ))

        saveUser(
            UserFactory.sample(
                identity = IdentityFactory.sample(username = "service-account-user", id = "service-account-user-id"),
                organisation = baseLtiOrganisation
            )
        )

        val responseBody = mvc.perform(
            MockMvcRequestBuilders
                .put("/v1/integrations/deployments")
                .content("""
                    {
                        "deploymentId": "deployment/id",
                        "externalUserId": "external-user,\\,//id"
                    }
                """.trimIndent())
                .asUserWithRoles("service-account-user-id", UserRoles.SYNCHRONISE_INTEGRATION_USERS)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(jsonPath("$.userId", Matchers.notNullValue()))
            .andReturn().response.contentAsString

        val organisations = organisationRepository.findOrganisationsByParentId(baseLtiOrganisation.id)
        Assertions.assertThat(organisations.size).isEqualTo(1)
        Assertions.assertThat(organisations[0].type()).isEqualTo(OrganisationType.LTI_DEPLOYMENT)
        Assertions.assertThat((organisations[0] as LtiDeployment).deploymentId).isEqualTo("deployment/id")
        Assertions.assertThat((organisations[0] as LtiDeployment).parent.name).isEqualTo("lti-integration-organisation")

        val users = userRepository.findAllByOrganisationId(organisations[0].id)
        Assertions.assertThat(users.size).isEqualTo(1)
        Assertions.assertThat(users[0].identity.username).isEqualTo("external-user,\\,//id")
        Assertions.assertThat(responseBody).contains("""{"userId":"${users[0].id.value}"}""")
    }

    @Test
    fun `returns forbidden when corresponding role not assigned`() {
        mvc.perform(
            MockMvcRequestBuilders
                .put("/v1/integrations/deployments")
                .content("""
                    {
                        "deploymentId": "deployment-id",
                        "externalUserId": "external-user-id"
                    }
                """.trimIndent())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isForbidden)
    }

    @Test
    fun `returns bad request when logged in user has no organisation`() {

        saveUser(
            UserFactory.sample(
                identity = IdentityFactory.sample(username = "service-account-user", id = "service-account-user-id"),
                organisation = null
            )
        )

        mvc.perform(
            MockMvcRequestBuilders
                .put("/v1/integrations/deployments")
                .content("""
                    {
                        "deploymentId": "deployment-id",
                        "externalUserId": "external-user-id"
                    }
                """.trimIndent())
                .asUserWithRoles("service-account-user-id", UserRoles.SYNCHRONISE_INTEGRATION_USERS)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }
}
