package com.boclips.users.presentation.resources

import com.boclips.users.domain.model.account.Account
import org.springframework.hateoas.Identifiable

class UserResource(
    private val id: String

) : Identifiable<String> {
    override fun getId() = id

    companion object {
        fun from(account: Account) =
            UserResource(account.id.value)
    }
}