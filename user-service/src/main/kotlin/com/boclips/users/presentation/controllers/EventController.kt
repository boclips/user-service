package com.boclips.users.presentation.controllers

import com.boclips.users.application.commands.TrackPageRenderedEvent
import com.boclips.users.application.commands.TrackUserExpiredEvent
import com.boclips.users.api.request.PageRenderedEventRequest
import com.boclips.users.application.commands.TrackPlatformInteractedWithEvent
import com.boclips.users.presentation.support.RefererHeaderExtractor
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1", "/v1/")
class EventController(
    val trackPageRenderedEvent: TrackPageRenderedEvent,
    val trackUserExpiredEvent: TrackUserExpiredEvent,
    val trackPlatformInteractedWithEvent: TrackPlatformInteractedWithEvent
) {

    @PostMapping("/events/page-render")
    fun logPageRenderedEvent(@RequestBody pageRenderedEvent: PageRenderedEventRequest?): ResponseEntity<Void> {
        trackPageRenderedEvent.invoke(pageRenderedEvent!!)
        return ResponseEntity(HttpStatus.CREATED)
    }

    @PostMapping("/events/expired-user-access")
    fun trackUserExpiredEvent(): ResponseEntity<Void> {
        trackUserExpiredEvent.invoke()
        return ResponseEntity(HttpStatus.CREATED)
    }

    @PostMapping("/events/platform-interaction")
    fun trackPlatformInteractedWithEvent(@RequestParam(required = true) subtype: String?): ResponseEntity<Void> {
        val refererUrl = RefererHeaderExtractor.getReferer()

        trackPlatformInteractedWithEvent(subtype!!, refererUrl)
        return ResponseEntity(HttpStatus.CREATED)
    }
}
