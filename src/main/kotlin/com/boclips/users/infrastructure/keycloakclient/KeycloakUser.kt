package com.boclips.users.infrastructure.keycloakclient

data class KeycloakUser(
        val username: String,
        val id: String?,
        val email: String? = null,
        val firstName: String? = null,
        val lastName: String? = null
)
