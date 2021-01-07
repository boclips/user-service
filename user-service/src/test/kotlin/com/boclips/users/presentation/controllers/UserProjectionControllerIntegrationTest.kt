package com.boclips.users.presentation.controllers

import com.boclips.users.domain.model.organisation.Address
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.asPublisher
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

class UserProjectionControllerIntegrationTest : AbstractSpringIntegrationTest() {

    @Test
    fun `should project organisation for PublisherProjection`() {
        val organisation = saveOrganisation(
            OrganisationFactory.district(
                address = Address(country = Country.usa(), state = State.fromCode("WA")),
            )
        )
        val user = saveUser(
            UserFactory.sample(
                organisation = organisation
            )
        )

        mvc.perform(
            MockMvcRequestBuilders.get("/v1/users/_self").asPublisher(user.id.value)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.equalTo(user.id.value)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.organisation.id").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.organisation.name").exists())
    }
}
