package com.boclips.users.domain.model.organisation

import com.boclips.users.domain.service.UniqueId
import org.bson.types.ObjectId

data class OrganisationId(val value: String) {
    companion object {
        operator fun invoke(): OrganisationId = OrganisationId(UniqueId())
    }

    fun isValid(): Boolean {
        return ObjectId.isValid(this.value)
    }
}
