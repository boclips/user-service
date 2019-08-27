package com.boclips.users.presentation.controllers

import com.boclips.users.application.CreateTeacherAccount
import com.boclips.users.application.GetUser
import com.boclips.users.application.SynchronisationService
import com.boclips.users.application.UpdateUser
import com.boclips.users.presentation.hateoas.UserLinkBuilder
import com.boclips.users.presentation.requests.CreateTeacherRequest
import com.boclips.users.presentation.requests.UpdateUserRequest
import com.boclips.users.presentation.resources.UserConverter
import com.boclips.users.presentation.resources.UserResource
import org.springframework.hateoas.ExposesResourceFor
import org.springframework.hateoas.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@ExposesResourceFor(UserResource::class)
@RequestMapping("/v1/users")
class UserController(
    private val createTeacherAccount: CreateTeacherAccount,
    private val updateUser: UpdateUser,
    private val getUser: GetUser,
    private val userConverter: UserConverter,
    private val userLinkBuilder: UserLinkBuilder,
    private val synchronisationService: SynchronisationService
) {

    @PostMapping
    fun createAUser(@Valid @RequestBody createTeacherRequest: CreateTeacherRequest?): ResponseEntity<Resource<*>> {
        val user = createTeacherAccount(createTeacherRequest!!)

        val headers = HttpHeaders()
        headers.set(HttpHeaders.LOCATION, userLinkBuilder.newUserProfileLink(user.id)?.href)

        return ResponseEntity(headers, HttpStatus.CREATED)
    }

    @PutMapping("/{id}")
    fun updateAUser(@PathVariable id: String, @Valid @RequestBody updateUserRequest: UpdateUserRequest): Resource<UserResource> {
        updateUser(id, updateUserRequest)
        return getAUser(id)
    }

    @GetMapping("/{id}")
    fun getAUser(@PathVariable id: String?): Resource<UserResource> {
        val user = getUser(id!!)
        return Resource(
            userConverter.toUserResource(user),
            userLinkBuilder.selfLink(),
            userLinkBuilder.profileLink()
        )
    }

    @PostMapping("/sync")
    fun syncCrmContacts() {
        synchronisationService.synchroniseCrmProfiles()
    }

    @PostMapping("/sync-identities")
    fun syncAccounts() {
        synchronisationService.synchroniseAccounts()
    }
}
