package com.boclips.users.presentation

import com.boclips.users.application.ContactsUpdater
import com.boclips.users.application.UserActions
import com.boclips.users.domain.model.account.Account
import mu.KLogging
import org.springframework.hateoas.ExposesResourceFor
import org.springframework.hateoas.Identifiable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@ExposesResourceFor(UserResource::class)
@RequestMapping("/v1/users")
class UserProfileController(
    private val userActions: UserActions,
    private val contactsUpdater: ContactsUpdater
) {
    companion object : KLogging()

    @PostMapping
    fun activateUser() = userActions.activateUser()

    @GetMapping("/{id}")
    fun getUserProfile(): Nothing? = null

    @PostMapping("/sync")
    fun syncUsers() {
        contactsUpdater.update()
    }
}

class UserResource(private val id: String) : Identifiable<String> {
    override fun getId() = id

    companion object {
        fun from(account: Account) = UserResource(account.id)
    }
}