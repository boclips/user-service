package com.boclips.users.presentation.controllers

import com.boclips.users.application.commands.CreateApiIntegration
import com.boclips.users.application.commands.GetApiIntegrationByName
import com.boclips.users.application.commands.GetAccountById
import com.boclips.users.presentation.annotations.BoclipsE2ETestSupport
import com.boclips.users.presentation.hateoas.AccountLinkBuilder
import com.boclips.users.presentation.requests.CreateAccountRequest
import com.boclips.users.presentation.resources.converters.AccountConverter
import com.boclips.users.presentation.resources.AccountResource
import org.springframework.hateoas.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@BoclipsE2ETestSupport
@Validated
@RestController
@RequestMapping("/v1", "/v1/")
class AccountTestSupportController(
    private val accountLinkBuilder: AccountLinkBuilder,
    private val createApiIntegration: CreateApiIntegration,
    private val getAccountById: GetAccountById,
    private val getApiIntegrationByName: GetApiIntegrationByName,
    private val accountConverter: AccountConverter
) {
    @PostMapping("/api-integrations")
    fun insertApiIntegration(@Valid @RequestBody request: CreateAccountRequest): ResponseEntity<Resource<*>> {
        val createdOrganisation = createApiIntegration(request)

        val headers = HttpHeaders()
        headers.set(HttpHeaders.LOCATION, accountLinkBuilder.self(createdOrganisation.id).href)

        return ResponseEntity(headers, HttpStatus.CREATED)
    }

    @GetMapping("/api-integrations")
    fun fetchApiIntegrationByName(@NotBlank @RequestParam(required = false) name: String?): Resource<AccountResource> {
        val apiIntegration = getApiIntegrationByName(name!!)

        return accountConverter.toResource(apiIntegration)
    }
}
