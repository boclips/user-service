package com.boclips.users.domain.service

import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.organisation.School
import org.springframework.stereotype.Service
import java.time.ZonedDateTime

@Service
class AccessExpiryService(val organisationRepository: OrganisationRepository) {
    fun userHasAccess(user: User): Boolean {
        val userAccessExpiry = user.accessExpiresOn ?: return true

        val organisationAccessExpiry =
            user.organisation?.let { organisation ->
                val district = (organisation.details as? School)?.district
                district?.accessExpiresOn ?: organisation.accessExpiresOn
            }

        val accessExpiresOn = getLatestDate(userAccessExpiry, organisationAccessExpiry)

        return accessExpiresOn.isAfter(ZonedDateTime.now())
    }

    private fun getLatestDate(left: ZonedDateTime, right: ZonedDateTime?): ZonedDateTime {
        if (right == null) {
            return left
        }

        return if (left.isAfter(right)) left else right
    }
}
