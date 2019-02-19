package com.boclips.users.domain.model.users

import com.boclips.users.infrastructure.keycloakclient.InvalidUserRepresentation
import org.keycloak.representations.idm.UserRepresentation


data class UserIdentity(
        val firstName: String,
        val lastName: String,
        val email: String,
        val id: String,
        val isVerified: Boolean
)