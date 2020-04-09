package com.boclips.users.domain.model.organisation

import com.boclips.users.domain.service.UniqueId

data class OrganisationId(val value: String) {
    companion object {
        operator fun invoke(): OrganisationId = OrganisationId(UniqueId())
    }
}
