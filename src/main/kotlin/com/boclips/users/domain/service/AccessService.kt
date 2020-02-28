package com.boclips.users.domain.service

import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.School
import org.springframework.stereotype.Service
import java.time.ZonedDateTime

@Service
class AccessService(val organisationRepository: OrganisationRepository) {
    fun userHasAccess(user: User): Boolean {
        val userAccessExpiry = user.accessExpiresOn ?: return true

        val organisation: Organisation<School>? =
            user.organisationId?.let { organisationRepository.findSchoolById(it) }

        val organisationAccessExpiry =
            organisation?.let {
                it.organisation.district?.accessExpiresOn ?: it.accessExpiresOn
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
