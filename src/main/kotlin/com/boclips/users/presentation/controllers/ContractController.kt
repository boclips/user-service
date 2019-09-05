package com.boclips.users.presentation.controllers

import com.boclips.users.application.commands.CreateContract
import com.boclips.users.application.commands.GetContract
import com.boclips.users.presentation.hateoas.ContractsLinkBuilder
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
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/v1/contracts/", "/v1/contracts")
class ContractController(
    private val createContract: CreateContract,
    private val getContract: GetContract,
    private val contractsLinkBuilder: ContractsLinkBuilder,
    private val contractConverter: ContractConverter
) {
    @PostMapping
    fun insertContract(@Valid @RequestBody request: CreateContractRequest): ResponseEntity<Resource<*>> {
        val createdContract = createContract(request)

        val headers = HttpHeaders()
        headers.set(HttpHeaders.LOCATION, contractsLinkBuilder.self(createdContract.id).href)

        return ResponseEntity(headers, HttpStatus.CREATED)
    }

    @GetMapping("/{id}")
    fun fetchContract(@PathVariable("id") id: String): ContractResource {
        val contract = getContract(id)

        return contractConverter.toResource(contract)
    }
}