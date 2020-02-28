package com.boclips.users.presentation.controllers

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.asUser
import com.boclips.users.testsupport.factories.OrganisationDetailsFactory
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

class SchoolControllerTest : AbstractSpringIntegrationTest() {
    @Test
    fun `lists schools when given only query and country - outside USA schools`() {
        val school = organisationRepository.save(
            school = OrganisationDetailsFactory.school(
                name = "my school 1",
                countryName = "GBR"
            )
        )
        organisationRepository.save(
            school = OrganisationDetailsFactory.school(
                name = "my school 2",
                countryName = "POL"
            )
        )
        organisationRepository.save(
            school = OrganisationDetailsFactory.school(
                name = "something else",
                countryName = "GBR"
            )
        )

        mvc.perform(MockMvcRequestBuilders.get("/v1/schools?countryCode=GBR&query=school").asUser("some-teacher"))
            .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.schools", Matchers.hasSize<Int>(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.schools[0].id", Matchers.equalTo(school.id.value)))
    }
}
