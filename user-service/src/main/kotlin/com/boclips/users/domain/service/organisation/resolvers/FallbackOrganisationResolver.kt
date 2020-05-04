package com.boclips.users.domain.service.organisation.resolvers

import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationRepository
import com.boclips.users.domain.model.organisation.OrganisationTag.DEFAULT_ORGANISATION
import com.boclips.users.domain.model.user.Identity
import mu.KLogging

class FallbackOrganisationResolver(val organisationRepository: OrganisationRepository) :
    OrganisationResolver {

    companion object : KLogging()

    override fun resolve(identity: Identity): Organisation? {
        val defaultOrganisations = organisationRepository.findByTag(DEFAULT_ORGANISATION)

        if (defaultOrganisations.size > 1) {
            logger.error { "Multiple default organisations" }
            return null
        }

        return defaultOrganisations.firstOrNull()
    }
}
