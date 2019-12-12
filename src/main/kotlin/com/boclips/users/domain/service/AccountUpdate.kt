package com.boclips.users.domain.service

import com.boclips.users.domain.model.account.AccountType
import com.boclips.users.domain.model.account.OrganisationAccountId
import java.time.ZonedDateTime

sealed class AccountUpdate(val id: OrganisationAccountId)

class AccountTypeUpdate(id: OrganisationAccountId, val type: AccountType) :
    AccountUpdate(id)

class AccountExpiresOnUpdate(id: OrganisationAccountId, val accessExpiresOn: ZonedDateTime) :
    AccountUpdate(id)
