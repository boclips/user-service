package com.boclips.users.application

import com.boclips.users.application.exceptions.IdentityNotFoundException
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.service.IdentityProvider
import com.boclips.users.domain.service.UserRepository
import com.boclips.users.domain.service.UserCreationService
import mu.KLogging
import org.springframework.stereotype.Component

@Component
class UserImportService(
    private val userRepository: UserRepository,
    private val userCreationService: UserCreationService,
    private val identityProvider: IdentityProvider
) {
    companion object : KLogging()

    fun importFromIdentityProvider(userIds: List<UserId>) {
        userIds.forEach {
            userRepository.findById(it) ?: importFromIdentityProvider(it)
        }
    }

    fun importFromIdentityProvider(userId: UserId): User {
        return identityProvider.getIdentitiesById(userId)?.let {
            userCreationService.create(it)
        } ?: throw IdentityNotFoundException(userId)
    }
}
