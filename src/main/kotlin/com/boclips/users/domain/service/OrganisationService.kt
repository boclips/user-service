package com.boclips.users.domain.service

import com.boclips.users.domain.model.organisation.District
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationAccount
import org.springframework.stereotype.Service

@Service
class OrganisationService(val organisationAccountRepository: OrganisationAccountRepository) {
    fun findByExternalId(externalId: String): OrganisationAccount? {
        return organisationAccountRepository.findByDistrictId(externalId)
    }
}