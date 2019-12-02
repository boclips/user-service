package com.boclips.users.application.commands

import com.boclips.users.domain.model.organisation.OrganisationAccount
import com.boclips.users.domain.service.OrganisationAccountRepository
import org.springframework.stereotype.Component

@Component
class SearchOrganisations(
    private val organisationAccountRepository: OrganisationAccountRepository
) {
    operator fun invoke(organisationName: String?, state: String?, countryCode: String?): List<OrganisationAccount<*>> {
        if (countryCode.isNullOrBlank()) {
            throw RuntimeException("You must provide a country code")
        }

        return organisationAccountRepository.findOrganisationAccountsByCountryCode(countryCode)!!
    }
}
