package com.boclips.users.presentation.controllers

import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.school.State
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.asUser
import com.boclips.users.testsupport.asUserWithRoles
import com.boclips.users.testsupport.factories.OrganisationFactory
import org.hamcrest.Matchers.endsWith
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

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
        val expiryTime = ZonedDateTime.now()
        val expiryTimeToString = expiryTime.format(DateTimeFormatter.ISO_INSTANT)

        val district = organisationAccountRepository.save(
            OrganisationFactory.district(
                name = "my district",
                externalId = "123",
                state = State(id = "FL", name = "Florida")
            ),
            accessExpiresOn = expiryTime
        )
        organisationAccountRepository.save(
            school = OrganisationFactory.school(
                name = "my district school",
                countryName = "USA",
                state = State(id = "FL", name = "Florida"),
                district = district
            )
        )
        val school = organisationAccountRepository.save(
            school = OrganisationFactory.school(
                name = "my independent school",
                countryName = "USA",
                state = State(id = "FL", name = "Florida"),
                district = null
            ),
            accessExpiresOn = expiryTime
        )
        mvc.perform(
            get("/v1/independent-organisations?countryCode=USA").asUserWithRoles(
                "some-boclipper",
                UserRoles.VIEW_ORGANISATIONS
            )
        )
            .andExpect(jsonPath("$._embedded.organisationAccountResourceList", hasSize<Int>(2)))
            .andExpect(
                jsonPath(
                    "$._embedded.organisationAccountResourceList[0].name",
                    equalTo(district.organisation.name)
                )
            )
            .andExpect(
                jsonPath(
                    "$._embedded.organisationAccountResourceList[0].type",
                    equalTo(district.organisation.type().toString())
                )
            )
            .andExpect(
                jsonPath(
                    "$._embedded.organisationAccountResourceList[0].accessExpiresOn",
                    equalTo(expiryTimeToString)
                )
            )
            .andExpect(
                jsonPath(
                    "$._embedded.organisationAccountResourceList[0]._links.self.href",
                    endsWith("/v1/organisations/${district.id.value}")
                )
            )
            .andExpect(
                jsonPath(
                    "$._embedded.organisationAccountResourceList[1].name",
                    equalTo(school.organisation.name)
                )
            )
            .andExpect(
                jsonPath(
                    "$._embedded.organisationAccountResourceList[1].type",
                    equalTo(school.organisation.type().toString())
                )
            )
            .andExpect(
                jsonPath(
                    "$._embedded.organisationAccountResourceList[1].accessExpiresOn",
                    equalTo(expiryTimeToString)
                )
            )
            .andExpect(
                jsonPath(
                    "$._embedded.organisationAccountResourceList[1]._links.self.href",
                    endsWith("/v1/organisations/${school.id.value}")
                )
            )
    }

    @Test
    fun `updating an organisation account`() {
        val expiryTime = ZonedDateTime.now()
        val expiryTimeToString = expiryTime.format(DateTimeFormatter.ISO_INSTANT)

        val district = organisationAccountRepository.save(
            OrganisationFactory.district(
                name = "my district",
                externalId = "123",
                state = State(id = "FL", name = "Florida")
            )
        )

        mvc.perform(
            put("/v1/organisations/${district.id.value}").asUserWithRoles(
                "some-boclipper",
                UserRoles.VIEW_ORGANISATIONS
            )
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """{"accessExpiresOn": "${expiryTimeToString}"}""".trimIndent()
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
    }
}
