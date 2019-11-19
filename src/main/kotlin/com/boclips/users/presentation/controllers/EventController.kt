package com.boclips.users.presentation.controllers

import com.boclips.users.application.commands.TrackPageRenderedEvent
import com.boclips.users.presentation.requests.PageRenderedEventRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1", "/v1/")
class EventController(
    val trackPageRenderedEvent: TrackPageRenderedEvent
) {

    @PostMapping("/events/page-render")
    fun logPageRenderedEvent(@RequestBody pageRenderedEvent: PageRenderedEventRequest): ResponseEntity<Void> {
        trackPageRenderedEvent.invoke(pageRenderedEvent)
        return ResponseEntity(HttpStatus.CREATED)
    }
}
