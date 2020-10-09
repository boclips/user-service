package com.boclips.users.application.commands

import com.boclips.users.application.model.OrganisationFilter
import com.boclips.users.domain.model.Page
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationRepository
import org.springframework.stereotype.Component

@Component
class GetOrganisations(private val organisationRepository: OrganisationRepository) {
    operator fun invoke(filter: OrganisationFilter): Page<Organisation> {
        return organisationRepository.findOrganisations(
            name = filter.name,
            countryCode = filter.countryCode,
            types = filter.organisationTypes,
            size = filter.size,
            page = filter.page
        )
    }
}
