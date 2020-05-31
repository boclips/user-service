package com.boclips.users.presentation.hateoas

import com.boclips.users.presentation.controllers.EventController
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder
import org.springframework.stereotype.Service

@Service
class EventLinkBuilder {
    fun logPageRenderedEventLink(): Link? {
        return WebMvcLinkBuilder.linkTo(
            WebMvcLinkBuilder.methodOn(EventController::class.java)
                .logPageRenderedEvent(null)
        ).withRel("trackPageRendered")
    }

    fun trackPlatformInteractedWithEventLink(): Link? {
        return WebMvcLinkBuilder.linkTo(
            WebMvcLinkBuilder.methodOn(EventController::class.java)
                .trackPlatformInteractedWithEvent(null, null)
        ).withRel("trackPlatformInteractedWith")
    }
}
