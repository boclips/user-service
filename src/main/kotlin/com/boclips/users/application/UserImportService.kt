package com.boclips.users.application

import com.boclips.users.application.exceptions.AccountNotFoundException
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.service.AccountProvider
import com.boclips.users.domain.service.UserRepository
import com.boclips.users.infrastructure.keycloak.UserAlreadyExistsException
import mu.KLogging
import org.springframework.stereotype.Component

@Component
class UserImportService(
    private val userRepository: UserRepository,
    private val accountProvider: AccountProvider
) {
    companion object : KLogging()

    fun importFromAccountProvider(userIds: List<UserId>) {
        userIds.forEach {
            userRepository.findById(it) ?: importFromAccountProvider(it)
        }
    }

    fun importFromAccountProvider(userId: UserId): User {
        if (userRepository.findById(userId) !== null) {
            throw UserAlreadyExistsException()
        }

        return accountProvider.getAccountById(userId)?.let { account ->
            userRepository.save(account)
        } ?: throw AccountNotFoundException(userId)
    }
}
