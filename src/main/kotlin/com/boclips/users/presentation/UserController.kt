package com.boclips.users.presentation

import com.boclips.users.application.ActivateUser
import com.boclips.users.application.UpdateContacts
import mu.KLogging
import org.springframework.hateoas.EntityLinks
import org.springframework.hateoas.ExposesResourceFor
import org.springframework.hateoas.Resource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@ExposesResourceFor(UserResource::class)
@RequestMapping("/v1/users")
class AccountProfileController(
    private val activateUser: ActivateUser,
    private val updateContacts: UpdateContacts,
    private val entityLinks: EntityLinks
) {
    companion object : KLogging()

    @PostMapping("/activate")
    fun activateUser(@RequestBody userActivationRequest: UserActivationRequest?): Resource<String> {
        val account = activateUser.activateUser(userActivationRequest)

        return Resource("", entityLinks.linkToSingleResource(UserResource(account.id.value)).withSelfRel())
    }

    @GetMapping("/{id}")
    fun getUserProfile(): Nothing? {
        return null
    }

    @PostMapping("/sync")
    fun syncUsers() {
        updateContacts.update()
    }
}
