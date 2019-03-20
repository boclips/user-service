package com.boclips.users.presentation

import com.boclips.users.application.UpdateContacts
import com.boclips.users.application.UserActions
import com.boclips.users.presentation.requests.CreateUserRequest
import com.boclips.users.presentation.resources.UserResource
import mu.KLogging
import org.springframework.hateoas.ExposesResourceFor
import org.springframework.hateoas.Link
import org.springframework.hateoas.Resource
import org.springframework.hateoas.mvc.ControllerLinkBuilder
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@ExposesResourceFor(UserResource::class)
@RequestMapping("/v1/users")
class UserController(
    private val userActions: UserActions,
    private val updateContacts: UpdateContacts
) {
    companion object : KLogging() {
        fun activateLink(): Link {
            return ControllerLinkBuilder.linkTo(
                ControllerLinkBuilder.methodOn(UserController::class.java)
                    .activateUser()
            ).withRel("activate")
        }

        fun createLink(): Link {
            return ControllerLinkBuilder.linkTo(
                ControllerLinkBuilder.methodOn(UserController::class.java)
                    .createUser(null)
            ).withRel("createAccount")
        }

        fun getLink(): Link {
            return ControllerLinkBuilder.linkTo(
                ControllerLinkBuilder.methodOn(UserController::class.java)
                    .getUser()
            ).withRel("profile")
        }
    }

    @PostMapping()
    fun createUser(@Valid @RequestBody createUserRequest: CreateUserRequest?): ResponseEntity<Resource<*>> {
        val user = userActions.create(createUserRequest!!)

        val resource = Resource(
            "", createLink()
        )

        return ResponseEntity(resource, HttpStatus.CREATED)
    }

    @PostMapping("/activate")
    fun activateUser(): Resource<String> {
        userActions.activate()

        return Resource("", activateLink(), getLink())
    }

    @GetMapping("/{id}")
    fun getUser(): Resource<String> {
        return Resource("", getLink())
    }

    @PostMapping("/sync")
    fun syncUsers() {
        updateContacts.update()
    }
}
