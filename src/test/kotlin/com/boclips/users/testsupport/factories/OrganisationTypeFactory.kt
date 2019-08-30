package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.OrganisationType
import org.bson.types.ObjectId

class OrganisationTypeFactory {
    companion object {
        fun api(organisationId: String = ObjectId().toHexString()): OrganisationType.ApiCustomer {
            return OrganisationType.ApiCustomer(organisationId = OrganisationIdFactory.sample(id = organisationId))
        }

        fun boclipsForTeachers(): OrganisationType.BoclipsForTeachers {
            return OrganisationType.BoclipsForTeachers
        }
    }
}
