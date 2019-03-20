package com.boclips.users.presentation

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.UserIdentityFactory
import com.boclips.users.testsupport.asUser
import org.hamcrest.Matchers.endsWith
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class UserControllerTest : AbstractSpringIntegrationTest() {

    @Test
    fun `can create a new user`() {
        mvc.perform(
            post("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"firstName": "jane",
                     "lastName": "doe",
                     "subjects": "some subjects",
                     "email": "jane@doe.com",
                     "password": "Champagn3"
                     }
                    """.trimIndent()
                )
        )
            .andExpect(status().isCreated)
    }

    @Test
    fun `user can be activated with referral code`() {
        identityProvider.createUser(UserIdentityFactory.sample(id = "registered-user"))

        mvc.perform(
            post("/v1/users/activate")
                .asUser("registered-user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"referralCode": "abc-123" }""")
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `user can be activated without referral code`() {
        mvc.perform(post("/v1/users/activate").asUser("activated-user"))
            .andExpect(status().isOk)
    }

    @Test
    fun `activate user endpoint contains self link`() {
        mvc.perform(post("/v1/users/activate").asUser("activated-user"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$._links.profile.href", endsWith("/users/{id}")))
    }

    @Test
    fun `activated user gets the correct links`() {
        identityProvider.createUser(UserIdentityFactory.sample(id = "activated-user"))

        mvc.perform(post("/v1/users/activate").asUser("activated-user"))
            .andExpect(status().isOk)

        mvc.perform(get("/v1/").asUser("activated-user"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$._links.profile.href", endsWith("/users/{id}")))
    }
}