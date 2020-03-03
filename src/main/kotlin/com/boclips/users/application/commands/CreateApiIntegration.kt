package com.boclips.users.application.commands

import com.boclips.users.application.exceptions.OrganisationAlreadyExistsException
import com.boclips.users.domain.model.contentpackage.AccessRuleId
import com.boclips.users.domain.model.organisation.ApiIntegration
import com.boclips.users.domain.model.organisation.DealType
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.service.OrganisationRepository
import com.boclips.users.presentation.requests.CreateOrganisationRequest
import org.springframework.stereotype.Service

@Service
class CreateApiIntegration(
    private val repository: OrganisationRepository
) {
    operator fun invoke(request: CreateOrganisationRequest): Organisation<ApiIntegration> {
        assertNewApiIntegrationDoesNotCollide(request)

        val organisation = Organisation(
            id = OrganisationId.create(),
            organisation = ApiIntegration(
                name = request.name ?: throw IllegalStateException("Name cannot be null")
            ),
            type = DealType.STANDARD,
            accessRuleIds = request.accessRuleIds?.map { AccessRuleId(it) }
                ?: throw IllegalStateException("Access rules cannot be null"),
            role = request.role,
            accessExpiresOn = null
        )

        return repository.save(organisation)
    }

    private fun assertNewApiIntegrationDoesNotCollide(request: CreateOrganisationRequest) {
        repository.findApiIntegrationByName(request.name!!)?.let {
            throw OrganisationAlreadyExistsException(request.name!!)
        }
        repository.findApiIntegrationByRole(request.role!!)?.let {
            throw OrganisationAlreadyExistsException(request.role!!)
        }
    }
}
