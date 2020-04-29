package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.service.OrganisationRepository

class RoleBasedOrganisationResolver(private val organisationRepository: OrganisationRepository) :
    OrganisationResolver {
    override fun resolve(roles: List<String>): Organisation? {
        val organisations = organisationRepository.findByRoleIn(roles)
        if(organisations.size > 1) {
            throw IllegalStateException("${roles.size} organisations match roles " +
                "[${roles.joinToString()}]: " +
                organisations.joinToString { it.id.value }
            )
        }
        return organisations.firstOrNull()
    }
}
