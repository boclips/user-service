package com.boclips.users.domain.model.account

import com.boclips.users.domain.service.UniqueId
import org.bson.types.ObjectId

data class AccountId(val value: String) {
    companion object {
        operator fun invoke(): AccountId = AccountId(UniqueId())
    }

    fun isValid(): Boolean {
        return ObjectId.isValid(this.value)
    }
}
