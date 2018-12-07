package com.boclips.users.application

import com.boclips.users.domain.service.UserService
import com.boclips.users.domain.model.users.IdentityProvider
import mu.KLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class UserRegistrator(
        val identityProvider: IdentityProvider,
        val userService: UserService
) {
    companion object : KLogging()

    @Scheduled(fixedDelayString = "\${app.registration-period-in-seconds}")
    fun registerNewTeachersSinceYesterday() = identityProvider
            .getLastAdditionsToTeacherGroup(LocalDate.now().minusDays(1))
            .apply { logger.info { "Found ${this.size} login events in the past 24 hours - checking registration state" } }
            .forEach { userService.registerUserIfNew(it) }

}