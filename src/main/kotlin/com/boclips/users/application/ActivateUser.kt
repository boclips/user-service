package com.boclips.users.application

import com.boclips.security.utils.UserExtractor
import com.boclips.users.domain.model.account.Account
import com.boclips.users.domain.model.account.AccountId
import com.boclips.users.domain.service.UserService
import com.boclips.users.presentation.SecurityContextUserNotFoundException
import com.boclips.users.presentation.UserActivationRequest
import org.springframework.stereotype.Component

@Component
class ActivateUser(
    private val userService: UserService
) {
    fun activateUser(userActivationRequest: UserActivationRequest?): Account {
        val authenticatedUser = UserExtractor.getCurrentUser() ?: throw SecurityContextUserNotFoundException()

        return userService.activate(AccountId(value = authenticatedUser.id))
    }
}