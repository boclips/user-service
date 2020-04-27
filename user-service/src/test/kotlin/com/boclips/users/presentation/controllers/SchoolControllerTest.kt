package com.boclips.users.presentation.controllers

import com.boclips.users.domain.model.organisation.Address
import com.boclips.users.domain.model.school.Country
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.asUser
import com.boclips.users.testsupport.factories.OrganisationFactory
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

class SchoolControllerTest : AbstractSpringIntegrationTest() {
    @Test
    fun `lists schools when given only query and country - outside USA schools`() {
        val school = organisationRepository.save(
            OrganisationFactory.school(
                name = "my school 1",
                address = Address(
                    country = Country.fromCode("GBR")
                )
            )
        )
        organisationRepository.save(
            OrganisationFactory.school(
                name = "my school 2",
                address = Address(
                    country = Country.fromCode("POL")
                )
            )
        )
        organisationRepository.save(
            OrganisationFactory.school(
                name = "something else",
                address = Address(
                    country = Country.fromCode("GBR")
                )
            )
        )

        mvc.perform(MockMvcRequestBuilders.get("/v1/schools?countryCode=GBR&query=school").asUser("some-teacher"))
            .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.schools", Matchers.hasSize<Int>(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.schools[0].id", Matchers.equalTo(school.id.value)))
    }
}
