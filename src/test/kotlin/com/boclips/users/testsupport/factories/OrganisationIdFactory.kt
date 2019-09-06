package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.organisation.OrganisationAccountId
import java.util.UUID

class OrganisationIdFactory {
    companion object {
        fun sample(
            id: String = UUID.randomUUID().toString()
        ) = OrganisationAccountId(value = id)
    }
}
