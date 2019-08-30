package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.service.OrganisationRepository
import com.boclips.users.infrastructure.keycloak.KeycloakWrapper
import org.springframework.stereotype.Service

@Service
class RoleBasedOrganisationIdResolver(private val organisationRepository: OrganisationRepository) :
    OrganisationIdResolver {
    override fun resolve(roles: List<String>): OrganisationId? {
        if (roles.isEmpty()) {
            return null
        }

        for (role in roles) {
            val organisation = organisationRepository.findByRole(role)
            if (organisation != null) {
                return organisation.id
            } else if (role == KeycloakWrapper.TEACHER_ROLE) {
                return null
            }
        }

        return null
    }
}
