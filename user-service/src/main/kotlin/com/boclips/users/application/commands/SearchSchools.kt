package com.boclips.users.application.commands

import com.boclips.users.domain.model.organisation.ExternalOrganisationId
import com.boclips.users.domain.model.organisation.ExternalOrganisationInformation
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.service.organisation.AmericanSchoolsProvider
import com.boclips.users.domain.model.organisation.OrganisationRepository
import org.springframework.stereotype.Component

@Component
class SearchSchools(
    private val organisationRepository: OrganisationRepository,
    private val americanSchoolsProvider: AmericanSchoolsProvider
) {
    operator fun invoke(schoolName: String?, countryCode: String?, state: String?): List<ExternalOrganisationInformation> {
        if (schoolName.isNullOrBlank() || countryCode.isNullOrBlank()) {
            throw RuntimeException("You must provide a school name and country code")
        }

        return when {
            Country.fromCode(countryCode).isUSA() -> {
                if (state.isNullOrBlank()) {
                    throw RuntimeException("You must provide a state for American schools")
                }
                americanSchoolsProvider.lookupSchools(stateId = state, schoolName = schoolName)
            }

            else -> organisationRepository.lookupSchools(
                schoolName = schoolName,
                countryCode = countryCode
            ).map { school -> ExternalOrganisationInformation(id = ExternalOrganisationId(school.id.value), name = school.name, address = school.address) }
        }
    }
}
