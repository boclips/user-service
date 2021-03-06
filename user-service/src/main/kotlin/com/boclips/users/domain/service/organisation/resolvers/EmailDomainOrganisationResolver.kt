package com.boclips.users.domain.service.organisation.resolvers

import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationRepository
import com.boclips.users.domain.model.user.Identity
import mu.KLogging

class EmailDomainOrganisationResolver(private val organisationRepository: OrganisationRepository) :
    OrganisationResolver {

    companion object : KLogging()

    override fun resolve(identity: Identity): Organisation? {
        val domain = identity.email?.split('@')?.get(1) ?: return null

        val organisations = organisationRepository.findByEmailDomain(domain)

        if (organisations.size > 1) {
            logger.warn { "Multiple organisations have email domain $domain" }
            return null
        }

        return organisations.firstOrNull()
    }
}
