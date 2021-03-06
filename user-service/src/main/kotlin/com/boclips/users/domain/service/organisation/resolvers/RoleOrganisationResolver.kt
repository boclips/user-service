package com.boclips.users.domain.service.organisation.resolvers

import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationRepository
import com.boclips.users.domain.model.user.Identity

class RoleOrganisationResolver(private val organisationRepository: OrganisationRepository) :
    OrganisationResolver {
    override fun resolve(identity: Identity): Organisation? {
        val organisations = organisationRepository.findByRoleIn(identity.roles)
        if (organisations.size > 1) {
            throw IllegalStateException("${identity.roles.size} organisations match roles " +
                "[${identity.roles.joinToString()}]: " +
                organisations.joinToString { it.id.value }
            )
        }
        return organisations.firstOrNull()
    }
}
