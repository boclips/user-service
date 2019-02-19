package com.boclips.users.domain.service

import com.boclips.users.domain.model.identity.Identity

interface CustomerManagementProvider {
    fun update(users: List<Identity>)
}