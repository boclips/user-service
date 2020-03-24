package com.boclips.users.presentation.controllers

import com.boclips.security.utils.UserExtractor
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.service.AccessExpiryService
import com.boclips.users.domain.service.UserRepository
import com.boclips.users.presentation.hateoas.AccessRuleLinkBuilder
import com.boclips.users.presentation.hateoas.CountryLinkBuilder
import com.boclips.users.presentation.hateoas.EventLinkBuilder
import com.boclips.users.presentation.hateoas.OrganisationLinkBuilder
import com.boclips.users.presentation.hateoas.UserLinkBuilder
import org.springframework.hateoas.EntityModel
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1", "/v1/")
class LinksController(
    private val userRepository: UserRepository,
    private val accessExpiryService: AccessExpiryService,
    private val userLinkBuilder: UserLinkBuilder,
    private val countryLinkBuilder: CountryLinkBuilder,
    private val accessRuleLinkBuilder: AccessRuleLinkBuilder,
    private val eventLinkBuilder: EventLinkBuilder,
    private val organisationLinkBuilder: OrganisationLinkBuilder
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
                        accessExpiryService.userHasAccess(it)
                    )
                },
                userLinkBuilder.profileLink(user?.id),
                userLinkBuilder.userLink(),
                userLinkBuilder.accessRulesLink(),
                countryLinkBuilder.getCountriesLink(),
                accessRuleLinkBuilder.searchAccessRules(),
                eventLinkBuilder.logPageRenderedEventLink(),
                organisationLinkBuilder.getOrganisationsLink(),
                organisationLinkBuilder.getOrganisationLink(),
                userLinkBuilder.validateShareCodeLink()
            )
        )
    }
}