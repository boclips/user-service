package com.boclips.users.infrastructure.keycloak

import com.boclips.users.application.exceptions.AlreadyExistsException

class UserAlreadyExistsException : AlreadyExistsException("User already exists")
