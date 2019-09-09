package com.boclips.users.presentation.hateoas

import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.presentation.controllers.ContractTestSupportController
import org.springframework.hateoas.Link
import org.springframework.hateoas.mvc.ControllerLinkBuilder
import org.springframework.stereotype.Service

@Service
class ContractsLinkBuilder {
    fun self(contractId: ContractId): Link {
        return ControllerLinkBuilder.linkTo(
            ControllerLinkBuilder.methodOn(ContractTestSupportController::class.java).fetchContract(contractId.value)
        ).withRel("self")
    }
}