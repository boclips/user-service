package com.boclips.users.domain.service

import com.boclips.users.domain.model.users.Identity

interface CustomerManagementProvider {
    fun update(users: List<Identity>)
}