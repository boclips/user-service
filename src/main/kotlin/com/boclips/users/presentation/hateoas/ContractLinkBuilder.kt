package com.boclips.users.presentation.hateoas

import com.boclips.security.utils.UserExtractor.getIfHasRole
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.presentation.controllers.contract.ContractTestSupportController
import com.boclips.users.presentation.controllers.contract.ContractsController
import org.springframework.hateoas.Link
import org.springframework.hateoas.mvc.ControllerLinkBuilder
import org.springframework.stereotype.Service

@Service
class ContractLinkBuilder {
    fun self(contractId: ContractId): Link {
        return ControllerLinkBuilder.linkTo(
            ControllerLinkBuilder.methodOn(ContractTestSupportController::class.java).fetchContract(contractId.value)
        ).withRel("self")
    }

    fun searchContracts(name: String? = null, rel: String? = null): Link? {
        return getIfHasRole(UserRoles.VIEW_CONTRACTS) {
            ControllerLinkBuilder.linkTo(
                ControllerLinkBuilder.methodOn(ContractsController::class.java).getContracts(name)
            ).withRel(rel ?: "searchContracts")
        }
    }
}