package com.boclips.users.application.commands

import com.boclips.eventbus.EventBus
import com.boclips.eventbus.events.contentpackage.ContentPackageBroadcastRequested
import com.boclips.users.domain.model.access.ContentPackageRepository
import com.boclips.users.domain.service.events.EventConverter
import mu.KLogging
import org.springframework.stereotype.Component

@Component
class BroadcastContentPackages(
    private val contentPackageRepository: ContentPackageRepository,
    private val eventBus: EventBus,
    private val eventConverter: EventConverter
) {
    companion object : KLogging()

    operator fun invoke() {
        logger.info { "Broadcasting all users" }

        contentPackageRepository.findAll()
            .map(eventConverter::toEventContentPackage)
            .map { contentPackage ->
                ContentPackageBroadcastRequested.builder()
                    .contentPackage(contentPackage)
                    .build()
            }
            .forEach { event -> eventBus.publish(event) }

        logger.info { "All users have been broadcast" }
    }
}
