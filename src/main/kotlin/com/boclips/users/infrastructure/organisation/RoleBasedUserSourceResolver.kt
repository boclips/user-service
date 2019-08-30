package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.OrganisationType
import com.boclips.users.domain.service.OrganisationRepository
import com.boclips.users.infrastructure.keycloak.KeycloakWrapper
import org.springframework.stereotype.Service

@Service
class RoleBasedUserSourceResolver(private val organisationRepository: OrganisationRepository) :
    UserSourceResolver {
    override fun resolve(roles: List<String>): OrganisationType? {
        if (roles.isEmpty()) {
            return null
        }

        for (role in roles) {
            val organisation = organisationRepository.findByRole(role)
            if (organisation != null) {
                return OrganisationType.ApiCustomer(organisationId = organisation.id)
            } else if (role == KeycloakWrapper.TEACHER_ROLE) {
                return OrganisationType.BoclipsForTeachers
            }
        }

        return null
    }
}
