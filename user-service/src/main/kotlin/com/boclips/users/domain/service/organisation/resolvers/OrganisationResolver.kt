package com.boclips.users.domain.service.organisation.resolvers

import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationRepository
import com.boclips.users.domain.model.user.Identity

interface OrganisationResolver {
    fun resolve(identity: Identity): Organisation?

    companion object {
        fun create(organisationRepository: OrganisationRepository): OrganisationResolver {
            val roleBasedResolver = RoleOrganisationResolver(organisationRepository)
            val fallbackResolver = FallbackOrganisationResolver(organisationRepository)
            val emailResolver = EmailDomainOrganisationResolver(organisationRepository)
            return OrganisationResolverChain(
                emailResolver,
                roleBasedResolver,
                fallbackResolver
            )
        }
    }
}
