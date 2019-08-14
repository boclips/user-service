package com.boclips.users.domain.service

import com.boclips.users.domain.model.organisation.Organisation

interface OrganisationRepository {
    fun save(organisationName: String, role: String? = null): Organisation
    fun findByRole(role: String): Organisation?
}
