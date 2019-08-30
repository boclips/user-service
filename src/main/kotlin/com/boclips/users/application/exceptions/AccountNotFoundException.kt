package com.boclips.users.application.exceptions

import com.boclips.users.domain.model.UserId

class AccountNotFoundException(id: UserId) : RuntimeException("Account $id cannot be found")
