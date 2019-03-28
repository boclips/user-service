package com.boclips.users.infrastructure

import com.boclips.users.domain.service.IdentityProvider
import com.boclips.users.domain.service.UserRepository
import io.micrometer.core.instrument.MeterRegistry
import mu.KLogging
import org.springframework.stereotype.Component

@Component
class MetricsUpdater(
    private val identityProvider: IdentityProvider,
    private val userRepository: UserRepository,
    private val registry: MeterRegistry
) {
    companion object : KLogging() {
        const val PREFIX = "boclips_"
    }

    fun update() {
        val identityCount = identityProvider.count()
        registry.gauge("${PREFIX}identities_count", identityCount)

        val usersCount = userRepository.count()
        registry.gauge("${PREFIX}users_count", usersCount)

        logger.info { "Updated metrics" }
    }
}
