package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationId
import org.bson.types.ObjectId

class OrganisationFactory {
    companion object {
        fun sample(
            name: String = "Amazing Organisation",
            id: OrganisationId = OrganisationId(value = ObjectId().toHexString())
        ): Organisation {
            return Organisation(
                id = id,
                name = name
            )
        }
    }
}
