package com.boclips.users.application

import com.boclips.users.application.exceptions.UserNotFoundException
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.service.IdentityProvider
import com.boclips.users.domain.service.UserRepository
import com.boclips.users.domain.service.convertIdentityToAccount
import com.boclips.users.infrastructure.keycloak.UserAlreadyExistsException
import mu.KLogging
import org.springframework.stereotype.Component

@Component
class UserImportService(
    private val userRepository: UserRepository,
    private val identityProvider: IdentityProvider
) {
    companion object : KLogging()

    fun importFromIdentityProvider(userIds: List<UserId>) {
        userIds.forEach {
            userRepository.findById(it) ?: importFromIdentityProvider(it)
        }
    }

    fun importFromIdentityProvider(userId: UserId): User {
        if (userRepository.findById(userId) !== null) {
            throw UserAlreadyExistsException()
        }

        return identityProvider.getUserById(userId)?.let { identity ->
            userRepository.save(convertIdentityToAccount(identity))
        } ?: throw UserNotFoundException(userId)
    }
}
