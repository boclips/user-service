package com.boclips.users.application.exceptions

class AccountNotFoundException(val id: String) : NotFoundException("Organisation $id not found")
