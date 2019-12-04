package com.boclips.users.domain.service

import com.boclips.users.domain.model.organisation.OrganisationAccountId
import com.boclips.users.domain.model.organisation.OrganisationAccountType
import java.time.ZonedDateTime

sealed class OrganisationAccountUpdate(val id: OrganisationAccountId)

class OrganisationAccountTypeUpdate(id: OrganisationAccountId, val type: OrganisationAccountType) :
    OrganisationAccountUpdate(id)

class OrganisationAccountExpiresOnUpdate(id: OrganisationAccountId, val accessExpiresOn: ZonedDateTime) :
    OrganisationAccountUpdate(id)
