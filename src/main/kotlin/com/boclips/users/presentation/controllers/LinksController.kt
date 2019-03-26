package com.boclips.users.presentation.controllers

import com.boclips.users.domain.service.UserRepository
import com.boclips.users.domain.model.UserId
import com.boclips.users.infrastructure.security.getCurrentUserIfNotAnonymous
import org.springframework.hateoas.Resource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1", "/v1/")
class LinksController(
    private val userRepository: UserRepository
) {
    @GetMapping
    fun getLinks(): Resource<String> {
        if (isUnauthenticated()) {
            return Resource("", listOf(UserController.createUserLink()))
        }

        if (!isActivated()) {
            return Resource(
                "",
                listOf(
                    UserController.activateUserLink(),
                    UserController.getUserLink()
                )
            )
        }

        if (isActivated()) {
            return Resource("", listOf(UserController.getUserLink()))
        }

        return Resource("", emptyList())
    }

    private fun isActivated(): Boolean {
        return getCurrentUserIfNotAnonymous()?.let { currentUser ->
            val user = userRepository.findById(UserId(value = currentUser.id))
            return user?.let { it.activated } ?: false
        } ?: return false
    }

    private fun isUnauthenticated(): Boolean {
        return getCurrentUserIfNotAnonymous() == null
    }
}