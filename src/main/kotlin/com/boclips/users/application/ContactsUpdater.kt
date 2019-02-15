package com.boclips.users.application

import com.boclips.users.domain.model.users.CustomerManagementProvider
import com.boclips.users.domain.model.users.IdentityProvider
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