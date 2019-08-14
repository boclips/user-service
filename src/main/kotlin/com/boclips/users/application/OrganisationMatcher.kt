package com.boclips.users.application

import com.boclips.users.domain.model.organisation.Organisation

interface OrganisationMatcher {
    fun match(roles: List<String>): Organisation?
}