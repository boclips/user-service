package com.boclips.users.application.exceptions

class ContractNotFoundException(val criteria: String) : RuntimeException("Contract not found for $criteria")