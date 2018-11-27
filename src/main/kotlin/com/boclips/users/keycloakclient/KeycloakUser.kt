package com.boclips.users.keycloakclient

data class KeycloakUser(
        val username: String,
        val id: String?,
        val email: String?,
        val firstName: String?,
        val lastName: String?
)
