package com.boclips.users.infrastructure.organisation

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
    override fun match(roles: List<String>): Organisation? {
        return userServiceProperties.organisationMappings
            .find { roles.contains(it.role) }
            ?.let { organisationRepository.findByName(it.organisationName) }
    }
}
