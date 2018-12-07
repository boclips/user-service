package com.boclips.users.infrastructure.keycloakclient

data class KeycloakUser(
        val username: String,
        val id: String? = null,
        val email: String? = null,
        val firstName: String? = null,
        val lastName: String? = null
)
