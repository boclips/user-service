package com.boclips.users.presentation

import com.boclips.users.domain.model.account.AccountId
import com.boclips.users.domain.model.account.AccountRepository
import com.boclips.users.infrastructure.security.getCurrentUserIfNotAnonymous
import org.springframework.hateoas.Resource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1", "/v1/")
class LinksController(
    private val accountRepository: AccountRepository
) {
    @GetMapping
    fun getLinks(): Resource<String> {
        if (isUnauthenticated()) {
            return Resource("", listOf(AccountProfileController.createLink()))
        }

        if (!isActivated()) {
            return Resource("", listOf(AccountProfileController.activateLink()))
        }

        if (isActivated()) {
            return Resource("", listOf(AccountProfileController.getLink()))
        }

        return Resource("", emptyList())
    }

    private fun isActivated(): Boolean {
        return getCurrentUserIfNotAnonymous()?.let { currentUser ->
            val user = accountRepository.findById(AccountId(value = currentUser.id))
            return user?.let { it.activated } ?: false
        } ?: return false
    }

    private fun isUnauthenticated(): Boolean {
        return return getCurrentUserIfNotAnonymous() == null
    }
}