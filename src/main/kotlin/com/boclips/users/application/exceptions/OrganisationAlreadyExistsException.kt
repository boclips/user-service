package com.boclips.users.application.exceptions

class OrganisationAlreadyExistsException(criteria: String) : AlreadyExistsException("Organisation exists for $criteria")