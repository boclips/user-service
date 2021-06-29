package com.boclips.users.presentation.controllers

import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.account.AccountProduct
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.asUserWithRoles
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

internal class AccountControllerIntegrationTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `gets all accounts`() {
        val accountOne =
            saveAccount(name = "greg's account", products = setOf(AccountProduct.API, AccountProduct.B2T))
        val accountTwo = saveAccount(name = "alex's account", products = setOf(AccountProduct.LTI))

        mockMvc.perform(
            MockMvcRequestBuilders.get("/v1/accounts").asUserWithRoles(
                id = "me",
                UserRoles.VIEW_ACCOUNTS
            )
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$._embedded.accounts", hasSize<Int>(2)))
            .andExpect(jsonPath("$._embedded.accounts[0].name", equalTo(accountOne.name)))
            .andExpect(jsonPath("$._embedded.accounts[1].name", equalTo(accountTwo.name)))
            .andExpect(jsonPath("$._embedded.accounts[0].products", equalTo(listOf("API", "B2T"))))
            .andExpect(jsonPath("$._embedded.accounts[1].products", equalTo(listOf("LTI"))))
    }
}
