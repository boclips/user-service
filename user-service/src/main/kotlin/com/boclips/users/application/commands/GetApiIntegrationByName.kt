package com.boclips.users.application.commands

import com.boclips.users.application.exceptions.OrganisationNotFoundException
import com.boclips.users.domain.model.organisation.ApiIntegration
import com.boclips.users.domain.model.organisation.OrganisationRepository
import org.springframework.stereotype.Service

@Service
class GetApiIntegrationByName(
    private val repository: OrganisationRepository
) {
    operator fun invoke(name: String): ApiIntegration {
        return repository.findApiIntegrationByName(name) ?: throw OrganisationNotFoundException(name)
    }
}
