package com.boclips.users.application

import com.boclips.security.utils.User
import com.boclips.users.domain.model.organisation.Organisation

interface OrganisationMatcher {
    fun match(user: User): Organisation?
}