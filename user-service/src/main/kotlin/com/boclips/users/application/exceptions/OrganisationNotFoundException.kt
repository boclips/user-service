package com.boclips.users.application.exceptions

class OrganisationNotFoundException(val id: String) : NotFoundException("Organisation $id not found")
