package com.boclips.users.presentation.hateoas

import com.boclips.security.utils.UserExtractor.getIfHasRole
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.accessrules.AccessRuleId
import com.boclips.users.presentation.controllers.accessrules.AccessRuleTestSupportController
import com.boclips.users.presentation.controllers.accessrules.AccessRulesController
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder
import org.springframework.stereotype.Service

@Service
class AccessRuleLinkBuilder {
    fun self(accessRuleId: AccessRuleId): Link {
        return WebMvcLinkBuilder.linkTo(
            WebMvcLinkBuilder.methodOn(AccessRuleTestSupportController::class.java).fetchAccessRule(accessRuleId.value)
        ).withRel("self")
    }

    fun searchAccessRules(name: String? = null, rel: String? = null): Link? {
        return getIfHasRole(UserRoles.VIEW_ACCESS_RULES) {
            WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(AccessRulesController::class.java).getAccessRules(name)
            ).withRel(rel ?: "searchAccessRules")
        }
    }
}
