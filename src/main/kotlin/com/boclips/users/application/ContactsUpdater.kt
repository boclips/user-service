package com.boclips.users.application

import com.boclips.users.domain.service.CustomerManagementProvider
import com.boclips.users.domain.service.IdentityProvider
import org.springframework.stereotype.Component

@Component
class ContactsUpdater(
    val identityProvider: IdentityProvider,
    val customerManagementProvider: CustomerManagementProvider
) {

    fun update() {
        val allUsers = identityProvider.getUsers()
        customerManagementProvider.update(allUsers)
    }
}