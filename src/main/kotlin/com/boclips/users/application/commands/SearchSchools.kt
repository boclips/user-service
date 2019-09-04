package com.boclips.users.application.commands

import com.boclips.users.domain.model.OrganisationType
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.service.OrganisationRepository
import org.springframework.stereotype.Component
import java.lang.RuntimeException

@Component
class SearchSchools(private val organisationRepository: OrganisationRepository) {
    operator fun invoke(school: String?, countryId: String?, state: String?): List<Organisation> {
        if (school.isNullOrBlank() || countryId.isNullOrBlank()) {
            throw RuntimeException("You must provide a school and country")
        }
        val country = Country.fromCode(countryId)

        if (country.isUSA()) {
            return emptyList()
        } else {
            return organisationRepository.findByNameAndCountry(
                organisationName = school,
                country = countryId
            )
        }
    }
}