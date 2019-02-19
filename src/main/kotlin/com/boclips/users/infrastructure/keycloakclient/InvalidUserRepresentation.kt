package com.boclips.users.infrastructure.keycloakclient

import java.lang.RuntimeException

class InvalidUserRepresentation(message: String) : RuntimeException(message)