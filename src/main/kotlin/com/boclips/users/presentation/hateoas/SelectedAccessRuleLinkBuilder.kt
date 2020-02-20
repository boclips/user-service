package com.boclips.users.presentation.hateoas

import com.boclips.users.presentation.controllers.accessrules.SelectedContentAccessRuleController
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder
import org.springframework.stereotype.Service

@Service
class SelectedAccessRuleLinkBuilder {
    fun addCollection(accessRuleId: String): Link {
        return WebMvcLinkBuilder.linkTo(
            WebMvcLinkBuilder.methodOn(SelectedContentAccessRuleController::class.java).addCollection(
                accessRuleId,
                null
            )
        ).withRel("addCollection")
    }

    fun removeCollection(accessRuleId: String): Link {
        return WebMvcLinkBuilder.linkTo(
            WebMvcLinkBuilder.methodOn(SelectedContentAccessRuleController::class.java).removeCollection(
                accessRuleId,
                null
            )
        ).withRel("removeCollection")
    }
}
