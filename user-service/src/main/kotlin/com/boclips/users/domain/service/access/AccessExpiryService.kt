package com.boclips.users.domain.service.access

import com.boclips.users.domain.model.organisation.OrganisationRepository
import com.boclips.users.domain.model.user.User
import org.springframework.stereotype.Service
import java.time.ZonedDateTime

@Service
class AccessExpiryService(val organisationRepository: OrganisationRepository) {
    fun userHasAccess(user: User): Boolean {
        return user.accessExpiresOn?.isAfter(ZonedDateTime.now()) ?: true
    }
}
