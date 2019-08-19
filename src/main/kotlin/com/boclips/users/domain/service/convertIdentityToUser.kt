package com.boclips.users.domain.service

import com.boclips.users.domain.model.Account
import com.boclips.users.domain.model.identity.Identity

fun convertIdentityToAccount(identity: Identity): Account {
    return Account(
        id = identity.id,
        username = identity.email,
        associatedTo = identity.associatedTo
    )
}
