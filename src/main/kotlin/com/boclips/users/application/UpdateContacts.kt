package com.boclips.users.application

import com.boclips.users.domain.service.CustomerManagementProvider
import com.boclips.users.domain.service.UserService
import org.springframework.stereotype.Component

@Component
class UpdateContacts(
    val userService: UserService,
    val customerManagementProvider: CustomerManagementProvider
) {
    fun update() {
        val allUsers = userService.findAllUsers()
        customerManagementProvider.update(allUsers)
    }
}