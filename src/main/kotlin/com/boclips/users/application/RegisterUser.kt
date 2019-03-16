package com.boclips.users.application

import com.boclips.users.domain.service.IdentityProvider
import com.boclips.users.domain.service.UserService
import mu.KLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class RegisterUser(
    val identityProvider: IdentityProvider,
    val userService: UserService
) {
    companion object : KLogging()

    @Scheduled(fixedDelayString = "\${app.registration-period-in-millis}")
    fun registerNewTeachersSinceYesterday() = identityProvider
        .getNewTeachers(LocalDate.now().minusDays(1))
        .apply { logger.info { "Found ${this.size} login events in the past 24 hours - checking registration state" } }
        .forEach { userService.registerUserIfNew(it.id) }
}