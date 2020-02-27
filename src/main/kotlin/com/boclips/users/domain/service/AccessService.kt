package com.boclips.users.domain.service

import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.account.Organisation
import com.boclips.users.domain.model.account.School
import org.springframework.stereotype.Service
import java.time.ZonedDateTime

@Service
class AccessService(val accountRepository: AccountRepository) {
    fun userHasAccess(user: User): Boolean {
        val userAccessExpiry = user.accessExpiresOn ?: return true

        val organisation: Organisation<School>? =
            user.organisationAccountId?.let { accountRepository.findSchoolById(it) }

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
