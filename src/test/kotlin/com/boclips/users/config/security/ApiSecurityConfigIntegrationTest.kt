package com.boclips.users.config.security

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.asBackofficeUser
import com.boclips.users.testsupport.asUser
import com.boclips.users.testsupport.factories.UserFactory
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ApiSecurityConfigIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `only user managers can synchronize users`() {
        mvc.perform(MockMvcRequestBuilders.post("/v1/users/sync"))
            .andExpect(status().isForbidden)

        mvc.perform(MockMvcRequestBuilders.post("/v1/users/sync").asBackofficeUser())
            .andExpect(status().is2xxSuccessful)
    }

    @Test
    fun `everybody can access actuator without permissions`() {
        mvc.perform(MockMvcRequestBuilders.get("/actuator/health"))
            .andExpect(status().is2xxSuccessful)
    }

    @Test
    fun `everybody can access links without permissions`() {
        mvc.perform(MockMvcRequestBuilders.get("/v1"))
            .andExpect(status().`is`(HttpStatus.OK.value()))

        mvc.perform(MockMvcRequestBuilders.get("/v1/"))
            .andExpect(status().`is`(HttpStatus.OK.value()))
    }

    @Test
    fun `everybody can access any endpoint with OPTIONS`() {
        mvc.perform(MockMvcRequestBuilders.options("/v1/users/activate"))
            .andExpect(status().is2xxSuccessful)
        mvc.perform(MockMvcRequestBuilders.options("/v1"))
            .andExpect(status().is2xxSuccessful)
    }

    @Test
    fun `only authenticated users can activate accounts`() {
        mvc.perform(MockMvcRequestBuilders.post("/v1/users/user-id"))
            .andExpect(status().isForbidden)

        saveUser(UserFactory.sample(id = "user-id"))
        mvc.perform(MockMvcRequestBuilders.put("/v1/users/user-id").asUser("user-id"))
            .andExpect(status().`is`(not401Or403()))
    }

    private fun not401Or403(): Matcher<Int> {
        return object : BaseMatcher<Int>() {
            override fun matches(item: Any?): Boolean {
                val statusActually = item as Int
                return statusActually != 403 && statusActually != 401
            }

            override fun describeTo(description: Description?) {
            }
        }
    }
}
