package com.boclips.users.presentation.hateoas

import com.boclips.security.utils.UserExtractor.getIfHasRole
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.presentation.controllers.ContractController
import org.springframework.hateoas.Link
import org.springframework.hateoas.mvc.ControllerLinkBuilder
import org.springframework.stereotype.Service

@Service
class ContractsLinkBuilder {
    fun self(contractId: ContractId): Link {
        return ControllerLinkBuilder.linkTo(
            ControllerLinkBuilder.methodOn(ContractController::class.java).fetchContract(contractId.value)
        ).withRel("self")
    }

    fun getContractByName(): Link? {
        return getIfHasRole(UserRoles.VIEW_CONTRACTS) {
            ControllerLinkBuilder.linkTo(
                ControllerLinkBuilder.methodOn(ContractController::class.java).fetchContractByName(null)
            ).withRel("getContractByName")
        }
    }
}