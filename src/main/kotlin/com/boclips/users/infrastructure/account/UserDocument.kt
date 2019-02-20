package com.boclips.users.infrastructure.account

import com.boclips.users.domain.model.account.Account
import com.boclips.users.domain.model.account.AccountId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "users")
data class UserDocument(
    @Id
    val id: String,
    val activated: Boolean
) {
    companion object {
        fun from(account: Account) = UserDocument(
            id = account.id.value,
            activated = account.activated
        )
    }

    fun toUser() = Account(id = AccountId(value = id), activated = activated)
}