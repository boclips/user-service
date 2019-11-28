package com.boclips.users.domain.service

import com.boclips.eventbus.EventBus
import com.boclips.eventbus.events.user.UserExpired
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.organisation.District
import com.boclips.users.domain.model.organisation.School
import com.boclips.users.domain.service.events.EventConverter
import org.springframework.stereotype.Service
import java.time.ZonedDateTime

@Service
class AccessService(
    private val organisationAccountRepository: OrganisationAccountRepository,
    private val eventBus: EventBus,
    private val eventConverter: EventConverter
) {
    fun userHasAccess(user: User): Boolean {
        val userAccessExpiry = user.accessExpiry ?: return true

        val organisationAccount =
            user.organisationAccountId?.let { organisationAccountRepository.findOrganisationAccountById(it) }

        val organisationAccessExpiry = when (val organisation = organisationAccount?.organisation) {
            is School -> organisation.district?.organisation?.accessExpiry ?: organisation.accessExpiry
            is District -> organisation.accessExpiry
            else -> null
        }

        val accessExpiry = getLatestDate(userAccessExpiry, organisationAccessExpiry)

        val userHasAccess = accessExpiry.isAfter(ZonedDateTime.now())

        if (!userHasAccess) {
            eventBus.publish(
                UserExpired.builder()
                    .user(eventConverter.toEventUser(user))
                    .build()
            )
        }

        return userHasAccess
    }

    private fun getLatestDate(left: ZonedDateTime, right: ZonedDateTime?): ZonedDateTime {
        if (right == null) {
            return left
        }

        return if (left.isAfter(right)) left else right
    }
}
