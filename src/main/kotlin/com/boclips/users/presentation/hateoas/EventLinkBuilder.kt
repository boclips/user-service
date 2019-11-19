package com.boclips.users.presentation.hateoas

import com.boclips.users.presentation.controllers.EventController
import org.springframework.hateoas.Link
import org.springframework.hateoas.mvc.ControllerLinkBuilder
import org.springframework.stereotype.Service

@Service
class EventLinkBuilder {
    fun logPageRenderedEventLink(): Link? {
        return ControllerLinkBuilder.linkTo(
            ControllerLinkBuilder.methodOn(EventController::class.java)
                .logPageRenderedEvent(null)).withRel("trackPageRendered")
    }
}
