package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.Platform
import org.bson.types.ObjectId

class UserSourceFactory {
    companion object {
        fun apiClientSample(organisationId: String = ObjectId().toHexString()): Platform.ApiCustomer {
            return Platform.ApiCustomer(organisationId = OrganisationIdFactory.sample(id = organisationId))
        }

        fun boclipsSample(): Platform.BoclipsForTeachers {
            return Platform.BoclipsForTeachers
        }
    }
}
