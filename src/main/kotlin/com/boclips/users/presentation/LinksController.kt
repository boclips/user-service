package com.boclips.users.presentation

import com.boclips.users.application.UserActions
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1", "/v1/")
class LinksController(private val userActions: UserActions) {

    @GetMapping
    fun getLinks() = userActions.getLinks()

}