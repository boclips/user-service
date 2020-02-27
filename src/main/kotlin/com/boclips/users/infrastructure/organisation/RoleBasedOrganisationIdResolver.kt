package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.account.OrganisationId
import com.boclips.users.domain.service.AccountRepository
import com.boclips.users.infrastructure.keycloak.KeycloakWrapper

class RoleBasedOrganisationIdResolver(private val accountRepository: AccountRepository) :
    OrganisationIdResolver {
    override fun resolve(roles: List<String>): OrganisationId? {
        if (roles.isEmpty()) {
            return null
        }

        for (role in roles) {
            val organisation = accountRepository.findApiIntegrationByRole(role)
            if (organisation != null) {
                return organisation.id
            } else if (role == KeycloakWrapper.TEACHER_ROLE) {
                return null
            }
        }

        return null
    }
}
