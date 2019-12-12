package com.boclips.users.application.exceptions

class AccountAlreadyExistsException(criteria: String) : AlreadyExistsException("Account exists for $criteria")
