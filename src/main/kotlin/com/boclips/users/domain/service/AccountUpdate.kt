package com.boclips.users.domain.service

import com.boclips.users.domain.model.account.DealType
import com.boclips.users.domain.model.account.OrganisationId
import java.time.ZonedDateTime

sealed class AccountUpdate(val id: OrganisationId)

class AccountTypeUpdate(id: OrganisationId, val type: DealType) :
    AccountUpdate(id)

class AccountExpiresOnUpdate(id: OrganisationId, val accessExpiresOn: ZonedDateTime) :
    AccountUpdate(id)
