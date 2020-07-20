package com.boclips.users.presentation.hateoas

import com.boclips.security.utils.UserExtractor.getIfHasRole
import com.boclips.users.config.security.UserRoles
import com.boclips.users.presentation.controllers.accessrules.AccessRulesController
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder
import org.springframework.stereotype.Service

@Service
class AccessRuleLinkBuilder {

    fun searchAccessRules(rel: String? = null): Link? {
        return getIfHasRole(UserRoles.VIEW_ACCESS_RULES) {
            WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(AccessRulesController::class.java).retrieveAccessRules()
            ).withRel(rel ?: "searchAccessRules")
        }
    }
}
