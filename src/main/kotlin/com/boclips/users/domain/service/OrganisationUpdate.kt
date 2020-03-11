package com.boclips.users.domain.service

import com.boclips.users.domain.model.organisation.DealType
import com.boclips.users.domain.model.organisation.OrganisationId
import java.time.ZonedDateTime

sealed class OrganisationUpdate(val id: OrganisationId)

class OrganisationTypeUpdate(id: OrganisationId, val type: DealType) :
    OrganisationUpdate(id)

class OrganisationExpiresOnUpdate(id: OrganisationId, val accessExpiresOn: ZonedDateTime) :
    OrganisationUpdate(id)

class OrganisationDomainOnUpdate(id: OrganisationId, val domain: String) :
    OrganisationUpdate(id)