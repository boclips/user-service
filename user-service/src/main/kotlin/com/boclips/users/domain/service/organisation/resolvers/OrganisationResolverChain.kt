package com.boclips.users.domain.service.organisation.resolvers

import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.user.Identity

class OrganisationResolverChain(private vararg val resolvers: OrganisationResolver) :
    OrganisationResolver {

    override fun resolve(identity: Identity): Organisation? {
        resolvers.forEach { resolver ->
            val organisation = resolver.resolve(identity)
            if(organisation != null) {
                return organisation
            }
        }
        return null
    }
}
