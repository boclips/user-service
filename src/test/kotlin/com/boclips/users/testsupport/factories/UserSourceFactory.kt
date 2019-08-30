package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.OrganisationType
import org.bson.types.ObjectId

class UserSourceFactory {
    companion object {
        fun apiClientSample(organisationId: String = ObjectId().toHexString()): OrganisationType.ApiCustomer {
            return OrganisationType.ApiCustomer(organisationId = OrganisationIdFactory.sample(id = organisationId))
        }

        fun boclipsSample(): OrganisationType.BoclipsForTeachers {
            return OrganisationType.BoclipsForTeachers
        }
    }
}
