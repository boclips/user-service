package com.boclips.users.presentation.controllers

import com.boclips.users.presentation.hateoas.ContractLinkBuilder
import com.boclips.users.presentation.hateoas.CountryLinkBuilder
import com.boclips.users.presentation.hateoas.EventLinkBuilder
import com.boclips.users.presentation.hateoas.UserLinkBuilder
import org.springframework.hateoas.Resource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1", "/v1/")
class LinksController(
    private val userLinkBuilder: UserLinkBuilder,
    private val countryLinkBuilder: CountryLinkBuilder,
    private val contractLinkBuilder: ContractLinkBuilder,
    private val eventLinkBuilder: EventLinkBuilder
) {
    @GetMapping
    fun getLinks() = Resource(
        "", listOfNotNull(
            userLinkBuilder.createUserLink(),
            userLinkBuilder.activateUserLink(),
            userLinkBuilder.renewAccessLink(),
            userLinkBuilder.profileLink(),
            userLinkBuilder.userLink(),
            userLinkBuilder.contractsLink(),
            countryLinkBuilder.getCountriesLink(),
            contractLinkBuilder.searchContracts(),
            eventLinkBuilder.logPageRenderedEventLink()
        )
    )
}
