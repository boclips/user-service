package com.boclips.users.presentation

import com.boclips.users.application.UpdateContacts
import com.boclips.users.application.UserActions
import com.boclips.users.presentation.requests.CreateUserRequest
import com.boclips.users.presentation.requests.UserActivationRequest
import com.boclips.users.presentation.resources.UserResource
import mu.KLogging
import org.springframework.hateoas.EntityLinks
import org.springframework.hateoas.ExposesResourceFor
import org.springframework.hateoas.Resource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@ExposesResourceFor(UserResource::class)
@RequestMapping("/v1/users")
class AccountProfileController(
    private val userActions: UserActions,
    private val updateContacts: UpdateContacts,
    private val entityLinks: EntityLinks
) {
    companion object : KLogging()

    @PostMapping()
    fun createUser(@RequestBody createUserRequest: CreateUserRequest): ResponseEntity<Resource<String>> {
        val user = userActions.create(createUserRequest)

        val resource = Resource(
            "",
            entityLinks.linkToSingleResource(UserResource(user.userId.value)).withSelfRel()
        )

        return ResponseEntity(resource, HttpStatus.CREATED)
    }

    @PostMapping("/activate")
    fun activateUser(@RequestBody userActivationRequest: UserActivationRequest?): Resource<String> {
        val account = userActions.activate(userActivationRequest)

        return Resource("", entityLinks.linkToSingleResource(
            UserResource(
                account.id.value
            )
        ).withSelfRel())
    }

    @GetMapping("/{id}")
    fun getUser(): Nothing? {
        return null
    }

    @PostMapping("/sync")
    fun syncUsers() {
        updateContacts.update()
    }
}
