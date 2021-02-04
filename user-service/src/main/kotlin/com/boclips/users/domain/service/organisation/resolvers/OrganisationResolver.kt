package com.boclips.users.domain.service.organisation.resolvers

import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationRepository
import com.boclips.users.domain.model.user.Identity

interface OrganisationResolver {
    fun resolve(identity: Identity): Organisation?

    companion object {
        fun create(organisationRepository: OrganisationRepository): OrganisationResolver {
            val legacyOrganisationResolver = LegacyOrganisationResolver(organisationRepository)
            val emailResolver = EmailDomainOrganisationResolver(organisationRepository)
            val roleBasedResolver = RoleOrganisationResolver(organisationRepository)
            val fallbackResolver = FallbackOrganisationResolver(organisationRepository)
            return OrganisationResolverChain(
                legacyOrganisationResolver,
                emailResolver,
                roleBasedResolver,
                fallbackResolver
            )
        }
    }
}
