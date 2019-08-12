package com.boclips.users.infrastructure.organisation

import com.boclips.security.utils.User
import com.boclips.users.application.OrganisationMatcher
import com.boclips.users.config.UserServiceProperties
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.service.OrganisationRepository
import org.springframework.stereotype.Service

@Service
class PreconfiguredOrganisationMatcher(
    private val userServiceProperties: UserServiceProperties,
    private val organisationRepository: OrganisationRepository
) : OrganisationMatcher {
    override fun match(user: User): Organisation? {
        return userServiceProperties.organisationMappings
            .find { user.hasRole(it.role) }
            ?.let { organisationRepository.findByName(it.organisationName) }
    }
}
