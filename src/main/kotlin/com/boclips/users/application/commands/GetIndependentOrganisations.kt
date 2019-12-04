package com.boclips.users.application.commands

import com.boclips.users.domain.model.organisation.OrganisationAccount
import com.boclips.users.domain.service.OrganisationAccountRepository
import org.springframework.stereotype.Component

@Component
class GetIndependentOrganisations(
    private val organisationAccountRepository: OrganisationAccountRepository
) {
    operator fun invoke(countryCode: String?): List<OrganisationAccount<*>> {
        if (countryCode.isNullOrBlank()) {
            throw RuntimeException("You must provide a country code")
        }

        return organisationAccountRepository.findIndependentSchoolsAndDistricts(countryCode)!!
    }
}
