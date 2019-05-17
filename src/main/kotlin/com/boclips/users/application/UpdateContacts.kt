package com.boclips.users.application

import com.boclips.users.domain.service.CustomerManagementProvider
import com.boclips.users.domain.service.UserService
import com.boclips.users.domain.service.userToCrmProfile
import org.springframework.stereotype.Component

@Component
class UpdateContacts(
    val userService: UserService,
    val customerManagementProvider: CustomerManagementProvider
) {
    operator fun invoke() {
        val allUsers = userService.findAllUsers()

        customerManagementProvider.update(allUsers.map { userToCrmProfile(it) })
    }
}