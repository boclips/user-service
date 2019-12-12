package com.boclips.users.application.exceptions

// TODO(AO): Rename this Account post Identity refactor
class OrganisationNotFoundException(val id: String) : NotFoundException("Organisation $id not found")
