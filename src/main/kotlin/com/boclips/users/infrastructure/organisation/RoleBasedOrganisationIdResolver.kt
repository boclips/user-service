package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.organisation.OrganisationAccountId
import com.boclips.users.domain.service.OrganisationAccountRepository
import com.boclips.users.infrastructure.keycloak.KeycloakWrapper
import org.springframework.stereotype.Service

@Service
class RoleBasedOrganisationIdResolver(private val organisationAccountRepository: OrganisationAccountRepository) :
    OrganisationIdResolver {
    override fun resolve(roles: List<String>): OrganisationAccountId? {
        if (roles.isEmpty()) {
            return null
        }

        for (role in roles) {
            val organisation = organisationAccountRepository.findByRole(role)
            if (organisation != null) {
                return organisation.id
            } else if (role == KeycloakWrapper.TEACHER_ROLE) {
                return null
            }
        }

        return null
    }
}
