package com.boclips.users.presentation.hateoas

import com.boclips.security.utils.UserExtractor.getIfHasRole
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.presentation.controllers.contract.ContractTestSupportController
import com.boclips.users.presentation.controllers.contract.ContractsController
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder
import org.springframework.stereotype.Service

@Service
class ContractLinkBuilder {
    fun self(contractId: ContractId): Link {
        return WebMvcLinkBuilder.linkTo(
            WebMvcLinkBuilder.methodOn(ContractTestSupportController::class.java).fetchContract(contractId.value)
        ).withRel("self")
    }

    fun searchContracts(name: String? = null, rel: String? = null): Link? {
        return getIfHasRole(UserRoles.VIEW_CONTRACTS) {
            WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(ContractsController::class.java).getContracts(name)
            ).withRel(rel ?: "searchContracts")
        }
    }
}
