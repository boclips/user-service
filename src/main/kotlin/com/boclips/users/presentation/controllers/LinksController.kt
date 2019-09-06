package com.boclips.users.presentation.controllers

import com.boclips.users.presentation.hateoas.ContractsLinkBuilder
import com.boclips.users.presentation.hateoas.OrganisationLinkBuilder
import com.boclips.users.presentation.hateoas.UserLinkBuilder
import org.springframework.hateoas.Resource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1", "/v1/")
class LinksController(
    private val userLinkBuilder: UserLinkBuilder,
    private val organisationLinkBuilder: OrganisationLinkBuilder,
    private val contractsLinkBuilder: ContractsLinkBuilder
) {
    @GetMapping
    fun getLinks() = Resource(
        "", listOfNotNull(
            userLinkBuilder.createUserLink(),
            userLinkBuilder.updateUserLink(),
            userLinkBuilder.profileLink(),
            userLinkBuilder.userLink(),
            userLinkBuilder.contractsLink(),
            organisationLinkBuilder.getCountriesLink(),
            organisationLinkBuilder.getOrganisationByName(),
            contractsLinkBuilder.getContractByName()
        )
    )
}
