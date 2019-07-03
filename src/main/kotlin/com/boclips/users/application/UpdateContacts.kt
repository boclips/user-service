package com.boclips.users.application

import com.boclips.users.domain.model.marketing.CrmProfile
import com.boclips.users.domain.service.MarketingService
import com.boclips.users.domain.service.SessionProvider
import com.boclips.users.domain.service.UserService
import com.boclips.users.domain.service.userToCrmProfile
import org.springframework.stereotype.Component

@Component
class UpdateContacts(
    val userService: UserService,
    val marketingService: MarketingService,
    val sessionProvider: SessionProvider
) {
    operator fun invoke() {
        val allCrmProfiles = userService.findAllUsers()
            .map { user ->
                val sessions = sessionProvider.getUserSessions(user.id)
                return@map userToCrmProfile(user, sessions)
            }

        marketingService.updateProfile(allCrmProfiles)

        updateSubscriptions(allCrmProfiles)
    }

    private fun updateSubscriptions(allCrmProfiles: List<CrmProfile>) {
        allCrmProfiles.forEach { crmProfile ->
            marketingService.updateSubscription(crmProfile)
        }
    }
}