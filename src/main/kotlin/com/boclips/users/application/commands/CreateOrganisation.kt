package com.boclips.users.application.commands

import com.boclips.users.domain.model.OrganisationType
import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.service.OrganisationRepository
import com.boclips.users.presentation.requests.CreateOrganisationRequest
import org.springframework.stereotype.Service

@Service
class CreateOrganisation(
    private val organisationRepository: OrganisationRepository
) {
    operator fun invoke(request: CreateOrganisationRequest): Organisation {
        return organisationRepository.save(
            organisationName = request.name!!,
            role = request.role,
            contractIds = request.contractIds!!.map { ContractId(it) },
            organisationType = OrganisationType.ApiCustomer
        )
    }
}