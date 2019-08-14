package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.UserSource

class UserSourceFactory {
    companion object {
        fun apiClientSample(organisationId: String): UserSource.ApiClient {
            return UserSource.ApiClient(organisationId = OrganisationIdFactory.sample(id = organisationId))
        }

        fun boclipsSample(): UserSource.Boclips {
            return UserSource.Boclips
        }
    }
}
