package com.boclips.users.application.commands

import com.boclips.users.application.exceptions.OrganisationNotFoundException
import com.boclips.users.domain.model.organisation.OrganisationAccount
import com.boclips.users.domain.model.organisation.OrganisationAccountId
import com.boclips.users.domain.service.OrganisationAccountRepository
import org.springframework.stereotype.Service

@Service
class GetOrganisationById(
    private val organisationRepository: OrganisationAccountRepository
) {
    operator fun invoke(id: String): OrganisationAccount<*> {
        return organisationRepository.findOrganisationAccountById(OrganisationAccountId(id)) ?: throw OrganisationNotFoundException(id)
    }
}