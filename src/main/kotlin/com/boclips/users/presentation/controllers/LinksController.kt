package com.boclips.users.presentation.controllers

import com.boclips.security.utils.UserExtractor
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.service.AccessService
import com.boclips.users.domain.service.UserRepository
import com.boclips.users.presentation.hateoas.AccountLinkBuilder
import com.boclips.users.presentation.hateoas.ContractLinkBuilder
import com.boclips.users.presentation.hateoas.CountryLinkBuilder
import com.boclips.users.presentation.hateoas.EventLinkBuilder
import com.boclips.users.presentation.hateoas.UserLinkBuilder
import org.springframework.hateoas.EntityModel
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1", "/v1/")
class LinksController(
    private val userRepository: UserRepository,
    private val accessService: AccessService,
    private val userLinkBuilder: UserLinkBuilder,
    private val countryLinkBuilder: CountryLinkBuilder,
    private val contractLinkBuilder: ContractLinkBuilder,
    private val eventLinkBuilder: EventLinkBuilder,
    private val accountLinkBuilder: AccountLinkBuilder
) {
    @GetMapping
    fun getLinks(): EntityModel<String> {
        val user = UserExtractor.getCurrentUser()?.let {
            userRepository.findById(UserId(value = it.id))
        }

        return EntityModel(
            "", listOfNotNull(
                userLinkBuilder.createUserLink(),
                userLinkBuilder.activateUserLink(user),
                user?.let {
                    userLinkBuilder.reportAccessExpiredLink(
                        userRepository.findById(it.id),
                        accessService.userHasAccess(it)
                    )
                },
                userLinkBuilder.profileLink(user?.id),
                userLinkBuilder.userLink(),
                userLinkBuilder.contractsLink(),
                countryLinkBuilder.getCountriesLink(),
                contractLinkBuilder.searchContracts(),
                eventLinkBuilder.logPageRenderedEventLink(),
                accountLinkBuilder.getIndependentAccountsLink(),
                accountLinkBuilder.getAccountLink(),
                userLinkBuilder.validateShareCodeLink()
            )
        )
    }
}
