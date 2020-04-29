package com.boclips.users.infrastructure.keycloak

class KeycloakCreateUserRequest(
    val email: String,
    val password: String,
    val role: String?
)
