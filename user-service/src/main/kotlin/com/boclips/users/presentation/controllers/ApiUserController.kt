package com.boclips.users.presentation.controllers

import com.boclips.users.api.request.CreateApiUserRequest
import com.boclips.users.api.response.user.UserResource
import com.boclips.users.application.commands.CreateApiUser
import com.boclips.users.application.exceptions.AlreadyExistsException
import org.springframework.hateoas.server.ExposesResourceFor
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@ExposesResourceFor(UserResource::class)
@RequestMapping("/v1/api-users")
class ApiUserController(
    private val createApiUser: CreateApiUser
) {

    @PutMapping("/{userId}")
    fun putApiUser(
        @PathVariable userId: String,
        @RequestBody createApiUserRequest: CreateApiUserRequest
    ): ResponseEntity<Any> {
        return try {
            createApiUser(userId, createApiUserRequest)
            ResponseEntity.status(HttpStatus.CREATED).build()
        } catch (e: AlreadyExistsException) {
            ResponseEntity.noContent().build()
        }
    }
}
