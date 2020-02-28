package com.boclips.users.presentation.controllers

import com.boclips.users.application.commands.GetOrganisationById
import com.boclips.users.application.commands.GetOrganisations
import com.boclips.users.application.commands.UpdateOrganisation
import com.boclips.users.application.model.OrganisationFilter
import com.boclips.users.presentation.requests.ListAccountsRequest
import com.boclips.users.presentation.requests.UpdateOrganisationRequest
import com.boclips.users.presentation.resources.AccountResource
import com.boclips.users.presentation.resources.converters.AccountConverter
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.PagedModel
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/v1", "/v1/")
class OrganisationController(
    private val getOrganisationById: GetOrganisationById,
    private val accountConverter: AccountConverter,
    private val updateOrganisation: UpdateOrganisation,
    private val getOrganisations: GetOrganisations
) {

    @GetMapping("/organisations/{id}")
    fun fetchOrganisationById(@PathVariable("id") id: String?): EntityModel<AccountResource> {
        val organisation = getOrganisationById(id!!)
        return accountConverter.toResource(organisation)
    }

    @GetMapping("/accounts/{id}")
    fun fetchAccountById(@PathVariable("id") id: String?): EntityModel<AccountResource> {
        return fetchOrganisationById(id)
    }

    @PatchMapping("/organisations/{id}")
    fun update(@PathVariable id: String, @Valid @RequestBody updateOrganisationRequest: UpdateOrganisationRequest?): EntityModel<AccountResource> {
        return accountConverter.toResource(updateOrganisation(id, updateOrganisationRequest))
    }

    @PatchMapping("/accounts/{id}")
    fun updateAnAccount(@PathVariable id: String, @Valid @RequestBody updateOrganisationRequest: UpdateOrganisationRequest?): EntityModel<AccountResource> {
        return update(id, updateOrganisationRequest)
    }

    @GetMapping("/organisations")
    fun fetchAll(listAccountsRequest: ListAccountsRequest?): PagedModel<EntityModel<AccountResource>> {
        val filter = OrganisationFilter(
            countryCode = listAccountsRequest?.countryCode,
            page = listAccountsRequest?.page ?: 0,
            size = listAccountsRequest?.size ?: 30
        )
        val accounts = getOrganisations(filter)

        val accountResources = accounts.map { account -> accountConverter.toResource(account) }

        return PagedModel(
            accountResources.content,
            PagedModel.PageMetadata(
                filter.size.toLong(),
                filter.page.toLong(),
                accountResources.totalElements
            )
        )
    }

    @GetMapping("/accounts")
    fun listAccounts(listAccountsRequest: ListAccountsRequest?): PagedModel<EntityModel<AccountResource>> {
        return fetchAll(listAccountsRequest)
    }
}
