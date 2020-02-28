package com.boclips.users.application.commands

import com.boclips.users.application.exceptions.OrganisationNotFoundException
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.service.OrganisationRepository
import org.springframework.stereotype.Service

@Service
class GetOrganisationById(
    private val repository: OrganisationRepository
) {
    operator fun invoke(id: String): Organisation<*> {
        return repository.findOrganisationById(OrganisationId(id)) ?: throw OrganisationNotFoundException(id)
    }
}
