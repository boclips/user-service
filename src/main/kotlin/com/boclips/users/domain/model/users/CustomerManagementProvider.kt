package com.boclips.users.domain.model.users

interface CustomerManagementProvider {
    fun update(users: List<UserIdentity>)
}