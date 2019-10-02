package com.boclips.users.presentation.controllers

import com.boclips.users.application.commands.CreateContract
import com.boclips.users.application.commands.GetContractById
import com.boclips.users.application.commands.GetContracts
import com.boclips.users.application.model.ContractFilter
import com.boclips.users.presentation.annotations.BoclipsE2ETestSupport
import com.boclips.users.presentation.hateoas.ContractResourcesHateoasWrapper
import com.boclips.users.presentation.hateoas.ContractResourcesWrapper
import com.boclips.users.presentation.hateoas.ContractLinkBuilder
import com.boclips.users.presentation.requests.CreateContractRequest
import com.boclips.users.presentation.resources.ContractConverter
import com.boclips.users.presentation.resources.ContractResource
import org.springframework.hateoas.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
@RestController
@RequestMapping("/v1/contracts")
class ContractTestSupportController(
    private val createContract: CreateContract,
    private val getContractById: GetContractById,
    private val getContracts: GetContracts,
    private val contractLinkBuilder: ContractLinkBuilder,
    private val contractConverter: ContractConverter
) {
    @PostMapping
    fun insertContract(@Valid @RequestBody request: CreateContractRequest): ResponseEntity<Resource<*>> {
        val createdContract = createContract(request)

        val headers = HttpHeaders()
        headers.set(HttpHeaders.LOCATION, contractLinkBuilder.self(createdContract.id).href)

        return ResponseEntity(headers, HttpStatus.CREATED)
    }

    @GetMapping("/{id}")
    fun fetchContract(@PathVariable("id") id: String): ContractResource {
        return contractConverter.toResource(
            getContractById(id)
        )
    }

    @GetMapping
    fun getContracts(@NotBlank @RequestParam(required = false) name: String?): ContractResourcesHateoasWrapper {
        val contrastFilter = ContractFilter(name = name)

        return ContractResourcesHateoasWrapper(
            ContractResourcesWrapper(
                getContracts(contrastFilter).map { contractConverter.toResource(it) }
            ),
            listOfNotNull(
                contractLinkBuilder.searchContracts(name = name, rel = "self")
            )
        )
    }
}