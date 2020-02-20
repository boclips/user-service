package com.boclips.users.application.exceptions

class AccessRuleNotFoundException(criteria: String) : NotFoundException("Access rule not found for $criteria")
