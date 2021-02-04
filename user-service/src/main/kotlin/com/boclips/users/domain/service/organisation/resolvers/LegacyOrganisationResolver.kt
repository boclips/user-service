package com.boclips.users.domain.service.organisation.resolvers

import com.boclips.users.domain.model.organisation.OrganisationRepository
import com.boclips.users.domain.model.user.Identity
import mu.KLogging

class LegacyOrganisationResolver(val organisationRepository: OrganisationRepository) :
    OrganisationResolver {

    companion object : KLogging()

    override fun resolve(identity: Identity) =
        identity.legacyOrganisationId
            ?.let { organisationRepository.findByLegacyId(it) }
}
