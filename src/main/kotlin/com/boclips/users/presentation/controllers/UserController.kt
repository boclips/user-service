package com.boclips.users.presentation.controllers

import com.boclips.users.application.ActivateUser
import com.boclips.users.application.CreateUser
import com.boclips.users.application.GetUser
import com.boclips.users.application.UpdateContacts
import com.boclips.users.presentation.requests.CreateUserRequest
import com.boclips.users.presentation.resources.UserResource
import mu.KLogging
import org.springframework.hateoas.ExposesResourceFor
import org.springframework.hateoas.Link
import org.springframework.hateoas.Resource
import org.springframework.hateoas.mvc.ControllerLinkBuilder
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@ExposesResourceFor(UserResource::class)
@RequestMapping("/v1/users")
class UserController(
    private val createUser: CreateUser,
    private val activateUser: ActivateUser,
    private val getUser: GetUser,
    private val updateContacts: UpdateContacts
) {
    companion object : KLogging() {
        fun activateUserLink(): Link {
            return ControllerLinkBuilder.linkTo(
                ControllerLinkBuilder.methodOn(UserController::class.java)
                    .activateAUser()
            ).withRel("activate")
        }

        fun createUserLink(): Link {
            return ControllerLinkBuilder.linkTo(
                ControllerLinkBuilder.methodOn(UserController::class.java)
                    .createAUser(null)
            ).withRel("createAccount")
        }

        fun getUserLink(): Link {
            return ControllerLinkBuilder.linkTo(
                ControllerLinkBuilder.methodOn(UserController::class.java)
                    .getAUser(null)
            ).withRel("profile")
        }

        fun getUserLink(id: String): Link {
            return ControllerLinkBuilder.linkTo(
                ControllerLinkBuilder.methodOn(UserController::class.java)
                    .getAUser(id)
            ).withRel("profile")
                .withSelfRel()
        }
    }

    @PostMapping
    fun createAUser(@Valid @RequestBody createUserRequest: CreateUserRequest?): ResponseEntity<Resource<*>> {
        val createdUser = createUser(createUserRequest!!)

        val resource = Resource(
            "",
            createUserLink(),
            getUserLink(createdUser.id.value)
        )

        val headers = HttpHeaders()
        headers.set(HttpHeaders.LOCATION, resource.getLink("self").href)

        return ResponseEntity(headers, HttpStatus.CREATED)
    }

    @PostMapping("/activate")
    fun activateAUser(): Resource<String> {
        activateUser()
        return Resource(
            "",
            activateUserLink(),
            getUserLink()
        )
    }

    @GetMapping("/{id}")
    fun getAUser(@PathVariable id: String?): Resource<UserResource> {
        val user = getUser(id!!)
        return Resource(user, getUserLink(id))
    }

    @PostMapping("/sync")
    fun syncUsers() {
        updateContacts()
    }
}
