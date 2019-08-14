package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.UserSource
import org.bson.types.ObjectId

class UserSourceFactory {
    companion object {
        fun apiClientSample(organisationId: String = ObjectId().toHexString()): UserSource.ApiClient {
            return UserSource.ApiClient(organisationId = OrganisationIdFactory.sample(id = organisationId))
        }

        fun boclipsSample(): UserSource.Boclips {
            return UserSource.Boclips
        }
    }
}
