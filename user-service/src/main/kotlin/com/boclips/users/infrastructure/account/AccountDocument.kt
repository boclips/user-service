package com.boclips.users.infrastructure.account

import com.boclips.users.domain.model.account.Account
import com.boclips.users.domain.model.account.AccountId
import org.bson.types.ObjectId

data class AccountDocument(
    val _id: ObjectId,
    val name: String
) {
    fun toAccount(): Account = Account(id = AccountId(this._id.toHexString()), name = this.name)
}
