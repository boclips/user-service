package com.boclips.users.application.commands

import com.boclips.users.application.exceptions.OrganisationNotFoundException
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.service.OrganisationRepository
import org.springframework.stereotype.Service

@Service
class GetOrganisationByName(
    private val organisationRepository: OrganisationRepository
) {
    operator fun invoke(name: String): Organisation {
        return organisationRepository.findByName(name) ?: throw OrganisationNotFoundException(name)
    }
}