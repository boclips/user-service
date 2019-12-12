package com.boclips.users.presentation.controllers

import com.boclips.users.application.commands.GetIndependentAccounts
import com.boclips.users.application.commands.UpdateAccount
import com.boclips.users.infrastructure.organisation.AccountSearchRequest
import com.boclips.users.presentation.hateoas.AccountLinkBuilder
import com.boclips.users.presentation.requests.UpdateAccountRequest
import com.boclips.users.presentation.resources.AccountResource
import com.boclips.users.presentation.resources.converters.AccountConverter
import org.springframework.hateoas.PagedResources
import org.springframework.hateoas.Resource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/v1", "/v1/")
class AccountController(
    private val getIndependentAccounts: GetIndependentAccounts,
    private val accountConverter: AccountConverter,
    private val updateAccount: UpdateAccount,
    private val accountLinkBuilder: AccountLinkBuilder
) {
    @GetMapping("/independent-accounts")
    fun getAllIndependentAccounts(
        @RequestParam(required = true) countryCode: String?,
        @RequestParam(required = false) page: Int? = null,
        @RequestParam(required = false) size: Int? = null
    ): PagedResources<Resource<AccountResource>> {
        val accounts =
            getIndependentAccounts(AccountSearchRequest(countryCode = countryCode!!, page = page, size = size))

        val accountResources = accounts.map { account -> accountConverter.toResource(account) }

        return PagedResources(
            accountResources.content,
            PagedResources.PageMetadata(size?.toLong() ?: 30, page?.toLong() ?: 0, accounts.totalElements),
            listOfNotNull(accountLinkBuilder.getNextPageLink(page ?: 0, accounts.totalPages))
        )
    }

    @PutMapping("/accounts/{id}")
    fun updateAnAccount(@PathVariable id: String, @Valid @RequestBody updateAccountRequest: UpdateAccountRequest?): Resource<AccountResource> {
        return accountConverter.toResource(updateAccount(id, updateAccountRequest))
    }
}
