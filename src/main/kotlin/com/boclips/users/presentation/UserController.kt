package com.boclips.users.presentation

import com.boclips.users.application.UpdateContacts
import com.boclips.users.application.UserActions
import com.boclips.users.presentation.requests.CreateUserRequest
import com.boclips.users.presentation.requests.UserActivationRequest
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
class AccountProfileController(
    private val userActions: UserActions,
    private val updateContacts: UpdateContacts
) {
    companion object : KLogging() {
        fun activateLink(): Link {
            return ControllerLinkBuilder.linkTo(
                ControllerLinkBuilder.methodOn(AccountProfileController::class.java)
                    .activateUser(null)
            ).withRel("activate")
        }

        fun createLink(): Link {
            return ControllerLinkBuilder.linkTo(
                ControllerLinkBuilder.methodOn(AccountProfileController::class.java)
                    .createUser(null)
            ).withRel("create")
        }

        fun getLink(): Link {
            return ControllerLinkBuilder.linkTo(
                ControllerLinkBuilder.methodOn(AccountProfileController::class.java)
                    .getUser()
            ).withRel("profile")
        }
    }

    @PostMapping()
    fun createUser(@Valid @RequestBody createUserRequest: CreateUserRequest?): ResponseEntity<Resource<String>> {
        val user = userActions.create(createUserRequest!!)

        val resource = Resource(
            "", createLink()
        )

        return ResponseEntity(resource, HttpStatus.CREATED)
    }

    @PostMapping("/activate")
    fun activateUser(@RequestBody userActivationRequest: UserActivationRequest?): Resource<String> {
        val account = userActions.activate(userActivationRequest)

        return Resource("", activateLink(), getLink())
    }

    @GetMapping("/{id}")
    fun getUser(): Resource<String> {
        return Resource("")
    }

    @PostMapping("/sync")
    fun syncUsers() {
        updateContacts.update()
    }
}
