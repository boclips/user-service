package com.boclips.users.presentation.controllers

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.asUser
import com.boclips.users.testsupport.factories.OrganisationFactory
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath

class OrganisationControllerIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `lists schools when given only query and country - outside USA schools`() {
        val school = organisationAccountRepository.save(
            school = OrganisationFactory.school(
                name = "my school 1",
                countryName = "GBR"
            )
        )
        organisationAccountRepository.save(
            school = OrganisationFactory.school(
                name = "my school 2",
                countryName = "POL"
            )
        )
        organisationAccountRepository.save(
            school = OrganisationFactory.school(
                name = "something else",
                countryName = "GBR"
            )
        )

        mvc.perform(get("/v1/schools?countryCode=GBR&query=school").asUser("some-teacher"))
            .andExpect(jsonPath("$._embedded.schools", hasSize<Int>(1)))
            .andExpect(jsonPath("$._embedded.schools[0].id", equalTo(school.id.value)))
    }
}