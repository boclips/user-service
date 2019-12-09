package com.boclips.users.application.commands

import com.boclips.users.domain.model.organisation.OrganisationAccount
import com.boclips.users.domain.service.OrganisationAccountRepository
import com.boclips.users.infrastructure.organisation.OrganisationSearchRequest
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component

@Component
class GetIndependentOrganisations(
    private val organisationAccountRepository: OrganisationAccountRepository
) {
    operator fun invoke(request : OrganisationSearchRequest): Page<OrganisationAccount<*>> {
        if (request.countryCode.isNullOrBlank()) {
            throw RuntimeException("You must provide a country code")
        }

        return organisationAccountRepository.findIndependentSchoolsAndDistricts(request)!!
    }
}
