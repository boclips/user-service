package com.boclips.users.application.commands

import com.boclips.users.application.exceptions.OrganisationNotFoundException
import com.boclips.users.domain.model.organisation.OrganisationAccount
import com.boclips.users.domain.service.OrganisationAccountRepository
import org.springframework.stereotype.Service

@Service
class GetOrganisationByName(
    private val organisationRepository: OrganisationAccountRepository
) {
    operator fun invoke(name: String): OrganisationAccount {
        return organisationRepository.findApiIntegrationByName(name) ?: throw OrganisationNotFoundException(name)
    }
}