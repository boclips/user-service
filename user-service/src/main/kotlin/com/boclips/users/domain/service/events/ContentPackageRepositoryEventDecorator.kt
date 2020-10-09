package com.boclips.users.domain.service.events

import com.boclips.eventbus.EventBus
import com.boclips.eventbus.events.contentpackage.ContentPackageUpdated
import com.boclips.users.domain.model.access.ContentPackage
import com.boclips.users.domain.model.access.ContentPackageRepository

class ContentPackageRepositoryEventDecorator(
    private val repository: ContentPackageRepository,
    private val eventBus: EventBus,
    private val eventConverter: EventConverter
) : ContentPackageRepository by repository {
    override fun replace(contentPackage: ContentPackage): ContentPackage? {
        val result = repository.replace(contentPackage)
        result?.run {
            eventConverter
                .toEventContentPackage(contentPackage)
                .let {
                    ContentPackageUpdated.builder()
                        .contentPackage(it)
                        .build()
                }
                .let(eventBus::publish)
        }
        return result
    }
}
