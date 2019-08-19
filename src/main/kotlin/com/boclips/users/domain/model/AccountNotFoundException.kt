package com.boclips.users.domain.model

class AccountNotFoundException(id: UserId) : RuntimeException("Account $id cannot be found")
