package com.boclips.users.application

import com.boclips.security.utils.UserExtractor
import com.boclips.users.domain.model.account.Account
import com.boclips.users.domain.model.account.AccountId
import com.boclips.users.domain.model.account.AccountRepository
import com.boclips.users.domain.service.UserService
import com.boclips.users.presentation.SecurityContextUserNotFoundException
import com.boclips.users.presentation.UserActivationRequest
import org.springframework.stereotype.Component

@Component
class ActivateUser(
    private val userService: UserService,
    private val accountRepository: AccountRepository
) {
    fun activateUser(userActivationRequest: UserActivationRequest?): Account {
        val authenticatedUser = UserExtractor.getCurrentUser() ?: throw SecurityContextUserNotFoundException()

        val activatedUser = userService.activate(AccountId(value = authenticatedUser.id))

        if (userActivationRequest?.referralCode != null) {
            accountRepository.markAsReferred(activatedUser.id)
        }

        return activatedUser
    }
}