package com.boclips.users.application.commands

import com.boclips.users.application.exceptions.OrganisationAlreadyExistsException
import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.domain.model.organisation.ApiIntegration
import com.boclips.users.domain.model.organisation.OrganisationAccount
import com.boclips.users.domain.service.OrganisationAccountRepository
import com.boclips.users.presentation.requests.CreateOrganisationRequest
import org.springframework.stereotype.Service

@Service
class CreateApiIntegration(
    private val organisationRepository: OrganisationAccountRepository
) {
    operator fun invoke(request: CreateOrganisationRequest): OrganisationAccount {
        assertNewApiIntegrationDoesNotCollide(request)

        return organisationRepository.save(
            apiIntegration = ApiIntegration(
                name = request.name!!
            ),
            role = request.role,
            contractIds = request.contractIds!!.map { ContractId(it) }
        )
    }

    private fun assertNewApiIntegrationDoesNotCollide(request: CreateOrganisationRequest) {
        organisationRepository.findApiIntegrationByName(request.name!!)?.let {
            throw OrganisationAlreadyExistsException(request.name!!)
        }
        organisationRepository.findApiIntegrationByRole(request.role!!)?.let {
            throw OrganisationAlreadyExistsException(request.role!!)
        }
    }
}