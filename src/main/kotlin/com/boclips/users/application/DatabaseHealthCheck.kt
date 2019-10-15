package com.boclips.users.application

import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.service.UserRepository
import mu.KLogging
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.stereotype.Component

@Component
class DatabaseHealthCheck(val userRepository: UserRepository) : HealthIndicator {
    companion object : KLogging()

    override fun health(): Health {
        try {
            userRepository.findById(UserId("non-existent-user"))
        } catch (ex: Exception) {
            logger.info(ex) { "Cannot retrieve users" }
            return Health.down().build()
        }

        return Health.up().build()
    }
}
