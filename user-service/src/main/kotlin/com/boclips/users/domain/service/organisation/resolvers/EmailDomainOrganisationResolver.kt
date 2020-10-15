package com.boclips.users.domain.service.organisation.resolvers

import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationRepository
import com.boclips.users.domain.model.user.Identity
import mu.KLogging
import org.litote.kmongo.out
import java.util.*

class EmailDomainOrganisationResolver(private val organisationRepository: OrganisationRepository) :
    OrganisationResolver {

    companion object : KLogging()

    override fun resolve(identity: Identity): Organisation? {
        val domain = identity.email?.split('@')?.get(1) ?: return null

        logger.info { "searching organisation by domain=$domain" }

        val organisations = organisationRepository.findByEmailDomain(domain)

        logger.info { "found orgs: ${printOrgs(organisations)}" }

        if (organisations.size > 1) {
            logger.warn { "Multiple organisations have email domain $domain" }
            return null
        }

        return organisations.firstOrNull()
    }

    private fun printOrgs(organisation: List<Organisation>):String {
        val output = StringJoiner(", ")
        organisation.forEach {
            output.add(it.id.value)
        }
        return output.toString()
    }
}
