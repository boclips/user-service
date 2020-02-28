package com.boclips.users.application.commands

import com.boclips.users.application.exceptions.OrganisationAlreadyExistsException
import com.boclips.users.domain.model.contentpackage.AccessRuleId
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.ApiIntegration
import com.boclips.users.domain.service.OrganisationRepository
import com.boclips.users.presentation.requests.CreateAccountRequest
import org.springframework.stereotype.Service

@Service
class CreateApiIntegration(
    private val repository: OrganisationRepository
) {
    operator fun invoke(request: CreateAccountRequest): Organisation<ApiIntegration> {
        assertNewApiIntegrationDoesNotCollide(request)

        return repository.save(
            apiIntegration = ApiIntegration(
                name = request.name!!
            ),
            accessRuleIds = request.accessRuleIds!!.map { AccessRuleId(it) },
            role = request.role
        )
    }

    private fun assertNewApiIntegrationDoesNotCollide(request: CreateAccountRequest) {
        repository.findApiIntegrationByName(request.name!!)?.let {
            throw OrganisationAlreadyExistsException(request.name!!)
        }
        repository.findApiIntegrationByRole(request.role!!)?.let {
            throw OrganisationAlreadyExistsException(request.role!!)
        }
    }
}
