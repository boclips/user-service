package com.boclips.users.presentation.controllers

import com.boclips.users.presentation.hateoas.UserLinkBuilder
import org.springframework.hateoas.Resource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1", "/v1/")
class LinksController(
    private val userLinkBuilder: UserLinkBuilder
) {
    @GetMapping
    fun getLinks() = Resource(
        "", listOfNotNull(
            userLinkBuilder.createUserLink(),
            userLinkBuilder.updateUserLink(),
            userLinkBuilder.profileLink()
        )
    )
}
