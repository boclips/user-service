package com.boclips.users.infrastructure.keycloakclient

import java.time.LocalDateTime

data class KeycloakUser(
        val username: String,
        val id: String? = null,
        val email: String? = null,
        val firstName: String? = null,
        val lastName: String? = null,
        val isEmailVerified: Boolean,
        val createdAccountAt: LocalDateTime
)
