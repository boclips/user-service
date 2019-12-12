package com.boclips.users.domain.service

import com.boclips.users.domain.model.account.AccountType
import com.boclips.users.domain.model.account.AccountId
import java.time.ZonedDateTime

sealed class AccountUpdate(val id: AccountId)

class AccountTypeUpdate(id: AccountId, val type: AccountType) :
    AccountUpdate(id)

class AccountExpiresOnUpdate(id: AccountId, val accessExpiresOn: ZonedDateTime) :
    AccountUpdate(id)
