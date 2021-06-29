package com.boclips.users.infrastructure.account

import com.boclips.users.domain.model.account.Account
import org.bson.types.ObjectId

data class AccountDocument(
    val _id: ObjectId,
    val name: String,
    val products: Set<String>?
) {
    fun toAccount(): Account = AccountDocumentConverter.toAccount(AccountDocument(_id, name, products))
}
