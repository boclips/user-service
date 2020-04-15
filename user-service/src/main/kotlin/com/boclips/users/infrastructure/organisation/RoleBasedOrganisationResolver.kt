package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.service.OrganisationRepository
import com.boclips.users.infrastructure.keycloak.KeycloakWrapper

class RoleBasedOrganisationResolver(private val organisationRepository: OrganisationRepository) :
    OrganisationResolver {
    override fun resolve(roles: List<String>): Organisation<*>? {
        if (roles.isEmpty()) {
            return null
        }

        for (role in roles) {
            val organisation = organisationRepository.findApiIntegrationByRole(role)
            if (organisation != null) {
                return organisation
            } else if (role == KeycloakWrapper.TEACHER_ROLE) {
                return null
            }
        }

        return null
    }
}
