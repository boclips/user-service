package com.boclips.users.presentation.controllers

import com.boclips.users.application.commands.GetAccounts
import com.boclips.users.presentation.converters.AccountConverter
import com.boclips.users.presentation.resources.AccountsResource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AccountController(private val getAccounts: GetAccounts, val accountConverter: AccountConverter) {

    @GetMapping("/v1/accounts")
    fun getAllAccounts(): AccountsResource = accountConverter.toAccountsResource(getAccounts())
}
