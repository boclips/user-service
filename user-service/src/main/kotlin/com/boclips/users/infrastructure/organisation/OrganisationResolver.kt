package com.boclips.users.infrastructure.organisation

import com.boclips.users.domain.model.Identity
import com.boclips.users.domain.model.organisation.Organisation

interface OrganisationResolver {
    fun resolve(identity: Identity): Organisation?
}
