package com.boclips.users.presentation.controllers

import com.boclips.users.application.commands.GetAccountById
import com.boclips.users.application.commands.GetAccounts
import com.boclips.users.application.commands.UpdateAccount
import com.boclips.users.application.model.OrganisationFilter
import com.boclips.users.presentation.requests.ListAccountsRequest
import com.boclips.users.presentation.requests.UpdateAccountRequest
import com.boclips.users.presentation.resources.AccountResource
import com.boclips.users.presentation.resources.converters.AccountConverter
import org.springframework.hateoas.PagedResources
import org.springframework.hateoas.Resource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/v1", "/v1/")
class AccountController(
    private val getAccountById: GetAccountById,
    private val accountConverter: AccountConverter,
    private val updateAccount: UpdateAccount,
    private val getAccounts: GetAccounts
) {

    @GetMapping("/accounts/{id}")
    fun fetchOrganisationById(@PathVariable("id") id: String?): Resource<AccountResource> {
        val organisation = getAccountById(id!!)

        return accountConverter.toResource(organisation)
    }

    @PatchMapping("/accounts/{id}")
    fun updateAnAccount(@PathVariable id: String, @Valid @RequestBody updateAccountRequest: UpdateAccountRequest?): Resource<AccountResource> {
        return accountConverter.toResource(updateAccount(id, updateAccountRequest))
    }

    @GetMapping("/accounts")
    fun listAccounts(listAccountsRequest: ListAccountsRequest?): PagedResources<Resource<AccountResource>> {
        val filter = OrganisationFilter(
            countryCode = listAccountsRequest?.countryCode,
            page = listAccountsRequest?.page ?: 0,
            size = listAccountsRequest?.size ?: 30
        )
        val accounts = getAccounts(filter)

        val accountResources = accounts.map { account -> accountConverter.toResource(account) }

        return PagedResources(
            accountResources.content,
            PagedResources.PageMetadata(
                filter.size.toLong(),
                filter.page.toLong(),
                accountResources.totalElements
            )
        )
    }
}
