package com.boclips.users.domain.service

import com.boclips.users.domain.model.organisation.Organisation
import org.springframework.stereotype.Service

@Service
class OrganisationService(val organisationRepository: OrganisationRepository) {
    fun findByExternalId(externalId: String): Organisation? {
        return organisationRepository.findByDistrictId(externalId)
    }
}