package com.boclips.users.domain.model.organisation

import org.bson.types.ObjectId

data class OrganisationId(val value: String) {
    companion object {
        fun create(): OrganisationId = OrganisationId(ObjectId.get().toHexString())
    }
}
