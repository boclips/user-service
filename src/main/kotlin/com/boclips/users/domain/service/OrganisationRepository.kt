package com.boclips.users.domain.service

import com.boclips.users.domain.model.organisation.Organisation

interface OrganisationRepository {
    fun save(organisationName: String): Organisation
    fun findByName(organisationName: String): Organisation?
}
