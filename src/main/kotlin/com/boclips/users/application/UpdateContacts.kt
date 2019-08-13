package com.boclips.users.application

import com.boclips.users.domain.service.MarketingService
import com.boclips.users.domain.service.SessionProvider
import com.boclips.users.domain.service.TeachersPlatformService
import com.boclips.users.domain.service.userToCrmProfile
import org.springframework.stereotype.Component

@Component
class UpdateContacts(
    val teachersPlatformService: TeachersPlatformService,
    val marketingService: MarketingService,
    val sessionProvider: SessionProvider
) {
    operator fun invoke() {
        val allCrmProfiles = teachersPlatformService.findAllUsers()
            .map { user ->
                val sessions = sessionProvider.getUserSessions(user.id)
                return@map userToCrmProfile(user, sessions)
            }
            .filter { it.isValid() }

        marketingService.updateProfile(allCrmProfiles)
    }
}
