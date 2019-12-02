package com.boclips.users.domain.service

import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.organisation.District
import com.boclips.users.domain.model.organisation.School
import org.springframework.stereotype.Service
import java.time.ZonedDateTime

@Service
class AccessService(val organisationAccountRepository: OrganisationAccountRepository) {
    fun userHasAccess(user: User): Boolean {
        val userAccessExpiry = user.accessExpiresOn ?: return true

        val organisationAccount =
            user.organisationAccountId?.let { organisationAccountRepository.findOrganisationAccountById(it) }

        val organisationAccessExpiry = when (val organisation = organisationAccount?.organisation) {
            is School -> organisation.district?.organisation?.accessExpiresOn ?: organisation.accessExpiresOn
            is District -> organisation.accessExpiresOn
            else -> null
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
