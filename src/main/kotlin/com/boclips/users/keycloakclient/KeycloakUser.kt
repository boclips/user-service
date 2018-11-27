package com.boclips.users.keycloakclient

data class KeycloakUser(
        val id: String,
        val email: String,
        val firstName: String,
        val lastName: String,
        val username: String
)
