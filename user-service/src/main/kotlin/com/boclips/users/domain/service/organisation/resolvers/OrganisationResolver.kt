package com.boclips.users.domain.service.organisation.resolvers

import com.boclips.users.domain.model.user.Identity
import com.boclips.users.domain.model.organisation.Organisation

interface OrganisationResolver {
    fun resolve(identity: Identity): Organisation?
}
