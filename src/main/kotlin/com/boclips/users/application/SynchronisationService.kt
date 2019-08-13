package com.boclips.users.application

import com.boclips.security.utils.UserExtractor
import com.boclips.users.application.exceptions.NotAuthenticatedException
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.UserNotFoundException
import com.boclips.users.domain.service.IdentityProvider
import com.boclips.users.domain.service.UserRepository
import com.boclips.users.domain.service.convertIdentityToUser
import org.springframework.stereotype.Component

@Component
class SynchronisationService(
    private val userRepository: UserRepository,
    private val identityProvider: IdentityProvider,
    private val organisationMatcher: OrganisationMatcher
) {

    fun synchroniseAll() {
        val allIdentities = identityProvider.getUsers()

        allIdentities.forEach {
            userRepository.findById(it.id) ?: synchronise(it.id)
        }
    }

    fun synchronise(userId: UserId): User {
        val authenticatedUser = UserExtractor.getCurrentUser() ?: throw NotAuthenticatedException()

        return identityProvider.getUserById(userId)?.let { identity ->
            userRepository.save(convertIdentityToUser(identity, organisationMatcher.match(authenticatedUser)?.id))
        } ?: throw UserNotFoundException(userId)
    }
}
