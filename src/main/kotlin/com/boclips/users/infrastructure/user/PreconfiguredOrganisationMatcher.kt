package com.boclips.users.infrastructure.user

import com.boclips.security.utils.User
import com.boclips.users.application.OrganisationMatcher
import com.boclips.users.config.UserServiceProperties
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.service.OrganisationRepository
import org.springframework.stereotype.Service

/**
 * Matches user roles to organisations based on static configuration.
 *
 * Added this as a first step, we probably want to switch to something better quickly.
 */
@Service
class PreconfiguredOrganisationMatcher(
    private val userServiceProperties: UserServiceProperties,
    private val organisationRepository: OrganisationRepository
) : OrganisationMatcher {
    override fun match(user: User): Organisation? =
        userServiceProperties.organisationMappings
            .find { user.hasRole(it.role) }
            ?.let { organisationRepository.findByName(it.organisationName) }
}
