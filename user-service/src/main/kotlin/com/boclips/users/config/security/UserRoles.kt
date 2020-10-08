package com.boclips.users.config.security

object UserRoles {
    const val ROLE_API = "API"
    const val ROLE_TEACHER = "TEACHER"
    const val ROLE_BOCLIPS_SERVICE = "BOCLIPS_SERVICE"
    const val ROLE_LTI = "LTI"
    const val BROADCAST_EVENTS = "BROADCAST_EVENTS"

    const val INSERT_ACCESS_RULES = "INSERT_ACCESS_RULES"
    const val VIEW_ACCESS_RULES = "VIEW_ACCESS_RULES"
    const val UPDATE_ACCESS_RULES = "UPDATE_ACCESS_RULES"

    const val VIEW_CONTENT_PACKAGES = "VIEW_CONTENT_PACKAGES"
    const val INSERT_CONTENT_PACKAGES = "INSERT_CONTENT_PACKAGES"
    const val UPDATE_CONTENT_PACKAGES = "UPDATE_CONTENT_PACKAGES"

    const val VIEW_USERS = "VIEW_USERS"
    const val UPDATE_USERS = "UPDATE_USERS"

    const val INSERT_ORGANISATIONS = "INSERT_ORGANISATIONS"
    const val VIEW_ORGANISATIONS = "VIEW_ORGANISATIONS"
    const val UPDATE_ORGANISATIONS = "UPDATE_ORGANISATIONS"

    const val SYNCHRONIZE_USERS_KEYCLOAK = "SYNCHRONIZE_USERS_KEYCLOAK"
    const val SYNCHRONIZE_USERS_HUBSPOT = "SYNCHRONIZE_USERS_HUBSPOT"

    const val SYNCHRONISE_INTEGRATION_USERS = "SYNCHRONISE_INTEGRATION_USERS"
}
