package com.boclips.users.application.exceptions

class ContractExistsException(name: String) : AlreadyExistsException("Contract with name $name already exists")
