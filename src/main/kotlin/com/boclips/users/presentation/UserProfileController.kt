package com.boclips.users.presentation

import com.boclips.users.domain.model.users.User
import org.springframework.hateoas.ExposesResourceFor
import org.springframework.hateoas.Identifiable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@ExposesResourceFor(UserResource::class)
@RequestMapping("/v1/users")
class UserProfileController {
    @GetMapping("/{id}")
    fun getUserProfile(): Nothing? = null
}

class UserResource(private val id: String) : Identifiable<String> {
    override fun getId() = id

    companion object {
        fun from(user: User) = UserResource(user.keycloakId.value)
    }
}