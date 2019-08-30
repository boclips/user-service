package com.boclips.users.application.exceptions

import java.lang.RuntimeException

class OrganisationNotFoundException(val id: String) : RuntimeException("Organisation $id not found") {
}