package com.boclips.users.application

import com.boclips.users.domain.service.UserService
import com.boclips.users.domain.model.users.IdentityProvider
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class UserRegistrator(
        val identityProvider: IdentityProvider,
        val userService: UserService
) {

    @Scheduled(fixedDelayString = "\${app.registration-period-in-seconds}")
    fun registerNewTeachersSinceYesterday() = identityProvider
            .getLastLoginUserIds("educators", LocalDate.now().minusDays(1))
            .forEach { userService.registerUserIfNew(it) }

}