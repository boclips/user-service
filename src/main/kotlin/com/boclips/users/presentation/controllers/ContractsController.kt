package com.boclips.users.presentation.controllers

import com.boclips.users.application.commands.GetContracts
import com.boclips.users.application.model.ContractFilter
import com.boclips.users.presentation.hateoas.ContractLinkBuilder
import com.boclips.users.presentation.hateoas.ContractResourcesHateoasWrapper
import com.boclips.users.presentation.hateoas.ContractResourcesWrapper
import com.boclips.users.presentation.resources.ContractConverter
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.constraints.NotBlank

@RestController
@RequestMapping("/v1/contracts")
class ContractsController(
    private val getContracts: GetContracts,
    private val contractConverter: ContractConverter,
    private val contractLinkBuilder: ContractLinkBuilder
) {
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