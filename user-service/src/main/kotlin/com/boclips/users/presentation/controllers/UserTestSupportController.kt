package com.boclips.users.presentation.controllers

import com.boclips.users.api.request.user.CreateE2EUserRequest
import com.boclips.users.application.commands.CreateE2EUser
import com.boclips.users.presentation.annotations.BoclipsE2ETestSupport
import com.boclips.users.presentation.hateoas.UserLinkBuilder
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@BoclipsE2ETestSupport
@RestController
@RequestMapping("/v1/e2e-users")
class UserTestSupportController(
    private val createE2EUser: CreateE2EUser,
    private val userLinkBuilder: UserLinkBuilder
) {
    @PostMapping
    fun createTestUser(@Valid @RequestBody userRequest: CreateE2EUserRequest?): ResponseEntity<Any> {
        val user = createE2EUser.invoke(userRequest!!)

        val headers = HttpHeaders()
        headers.set(HttpHeaders.LOCATION, userLinkBuilder.newUserProfileLink(user.id)?.href)

        return ResponseEntity(headers, HttpStatus.CREATED)
    }
}
