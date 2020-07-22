package com.boclips.users.presentation.controllers

import com.boclips.security.utils.UserExtractor
import com.boclips.users.domain.model.user.UserId
import com.boclips.users.domain.service.access.AccessExpiryService
import com.boclips.users.domain.model.user.UserRepository
import com.boclips.users.presentation.hateoas.*
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
    private val organisationLinkBuilder: OrganisationLinkBuilder,
    private val contentPackageLinkBuilder: ContentPackageLinkBuilder
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
                eventLinkBuilder.trackPlatformInteractedWithEventLink(),
                organisationLinkBuilder.getOrganisationsLink(),
                organisationLinkBuilder.getOrganisationLink(),
                userLinkBuilder.validateShareCodeLink(),
                contentPackageLinkBuilder.getContentPackagesLink(),
                contentPackageLinkBuilder.getContentPackageLink()
            )
        )
    }
}
