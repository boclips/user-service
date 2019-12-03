package com.boclips.users.presentation.controllers

import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.organisation.District
import com.boclips.users.domain.model.school.State
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.asBackofficeUser
import com.boclips.users.testsupport.asUser
import com.boclips.users.testsupport.asUserWithRoles
import com.boclips.users.testsupport.factories.OrganisationFactory
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import java.time.ZonedDateTime

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

    @Test
    fun `lists all independent US schools and organisations`() {
        val district = organisationAccountRepository.save(
            District(name = "my district", externalId = "123", state = State(id = "FL", name = "Florida"), accessExpiry = ZonedDateTime.now())
        )
        organisationAccountRepository.save(
            school = OrganisationFactory.school(
                name = "my district school",
                countryName = "USA",
                state = State(id = "FL", name = "Florida"),
                district = district,
                accessExpiry = ZonedDateTime.now()
            )
        )
        val school = organisationAccountRepository.save(
            school = OrganisationFactory.school(
                name = "my independent school",
                countryName = "USA",
                state = State(id = "FL", name = "Florida"),
                district = null,
                accessExpiry = ZonedDateTime.now()
            )
        )
        mvc.perform(get("/v1/organisations?countryCode=USA").asUserWithRoles("some-boclipper", UserRoles.VIEW_ORGANISATIONS))
            .andExpect(jsonPath("$._embedded.organisationAccountResourceList", hasSize<Int>(2)))
            .andExpect(jsonPath("$._embedded.organisationAccountResourceList[0].name", equalTo(district.organisation.name)))
            .andExpect(jsonPath("$._embedded.organisationAccountResourceList[0].type", equalTo(district.type.toString())))
            .andExpect(jsonPath("$._embedded.organisationAccountResourceList[0].accessExpiry", equalTo(district.organisation.accessExpiry.toString())))
            .andExpect(jsonPath("$._embedded.organisationAccountResourceList[1].name", equalTo(school.organisation.name)))
            .andExpect(jsonPath("$._embedded.organisationAccountResourceList[1].type", equalTo(school.type.toString())))
            .andExpect(jsonPath("$._embedded.organisationAccountResourceList[1].accessExpiry", equalTo(school.organisation.accessExpiry.toString())))
    }
}
