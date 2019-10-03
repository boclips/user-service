package com.boclips.users.presentation.hateoas

import com.boclips.users.presentation.controllers.contract.SelectedContentContractController
import org.springframework.hateoas.Link
import org.springframework.hateoas.mvc.ControllerLinkBuilder
import org.springframework.stereotype.Service

@Service
class SelectedContractLinkBuilder {
    fun addCollection(contractId: String): Link {
        return ControllerLinkBuilder.linkTo(
            ControllerLinkBuilder.methodOn(SelectedContentContractController::class.java).addCollection(
                contractId,
                null
            )
        ).withRel("addCollection")
    }

    fun removeCollection(contractId: String): Link {
        return ControllerLinkBuilder.linkTo(
            ControllerLinkBuilder.methodOn(SelectedContentContractController::class.java).removeCollection(
                contractId,
                null
            )
        ).withRel("removeCollection")
    }
}