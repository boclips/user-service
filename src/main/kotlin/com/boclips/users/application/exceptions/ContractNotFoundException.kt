package com.boclips.users.application.exceptions

class ContractNotFoundException(criteria: String) : NotFoundException("Contract not found for $criteria")
