package com.boclips.users.application.exceptions

class ContractNotFoundException(val criteria: String) : NotFoundException("Contract not found for $criteria")