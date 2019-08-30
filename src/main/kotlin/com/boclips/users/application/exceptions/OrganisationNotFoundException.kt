package com.boclips.users.application.exceptions

class OrganisationNotFoundException(val id: String) : RuntimeException("Organisation $id not found") {
}