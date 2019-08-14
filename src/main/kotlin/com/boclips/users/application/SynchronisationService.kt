package com.boclips.users.application

import com.boclips.users.domain.service.MarketingService
import com.boclips.users.domain.service.SessionProvider
import com.boclips.users.domain.service.TeachersPlatformService
import com.boclips.users.domain.service.convertUserToCrmProfile
import org.springframework.stereotype.Component

@Component
class SynchronisationService(
    val teachersPlatformService: TeachersPlatformService,
    val marketingService: MarketingService,
    val sessionProvider: SessionProvider
) {
    fun synchroniseTeachers() {
        val allCrmProfiles = teachersPlatformService.findAllTeachers()
            .map { user ->
                val sessions = sessionProvider.getUserSessions(user.id)
                return@map convertUserToCrmProfile(user, sessions)
            }
            .filter { it.isValid() }

        marketingService.updateProfile(allCrmProfiles)
    }
}
