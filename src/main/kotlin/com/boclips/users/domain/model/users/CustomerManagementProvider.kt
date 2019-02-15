package com.boclips.users.domain.model.users

import com.boclips.users.infrastructure.keycloakclient.KeycloakUser

interface CustomerManagementProvider {
    fun update(users: List<KeycloakUser>)
}