package com.boclips.users.application.commands

import com.boclips.users.domain.model.LookupEntry
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.service.OrganisationAccountRepository
import org.springframework.stereotype.Component
import java.lang.RuntimeException

@Component
class SearchSchools(private val organisationAccountRepository: OrganisationAccountRepository) {
    operator fun invoke(school: String?, countryId: String?, state: String?): List<LookupEntry> {
        if (school.isNullOrBlank() || countryId.isNullOrBlank()) {
            throw RuntimeException("You must provide a school and country")
        }
        val country = Country.fromCode(countryId)

        if (country.isUSA()) {
            return emptyList()
        } else {
            return organisationAccountRepository.lookupSchools(
                organisationName = school,
                country = countryId
            )
        }
    }
}