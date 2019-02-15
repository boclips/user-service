package com.boclips.users.domain.model.users

import java.time.LocalDateTime

data class User(
    val keycloakId: KeycloakId,
    val mixpanelId: String?,
    val activated: Boolean,
    val firstName: String,
    val lastName: String,
    val email: String,
    val subjects: String,
    val createdAt: LocalDateTime
)