package com.boclips.users.application

import com.boclips.users.config.SchedulerProperties
import com.boclips.users.domain.model.users.IdentityProvider
import com.boclips.users.domain.service.UserService
import mu.KLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDateTime

@Component
class UserRegistrator(
    val identityProvider: IdentityProvider,
    val userService: UserService,
    val schedulerProperties: SchedulerProperties
) {
    companion object : KLogging()

    @Scheduled(fixedDelayString = "\${app.registration-period-in-millis}")
    fun registerNewTeachersSinceLastPoll() = identityProvider
        .getUsersRegisteredSince(
            LocalDateTime.now().minus(
                Duration.ofMillis(schedulerProperties.registrationPeriodInMillis.toLong())
            )
        )
        .apply { logger.info { "Found ${this.size} register events in the past 24 hours - checking registration state" } }
        .forEach { userService.registerUserIfNew(it.keycloakId.value) }
}