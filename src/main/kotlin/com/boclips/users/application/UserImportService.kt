package com.boclips.users.application

import com.boclips.security.utils.UserExtractor
import com.boclips.users.application.exceptions.NotAuthenticatedException
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.UserNotFoundException
import com.boclips.users.domain.service.IdentityProvider
import com.boclips.users.domain.service.UserRepository
import com.boclips.users.domain.service.convertIdentityToUser
import com.boclips.users.infrastructure.keycloak.UserAlreadyExistsException
import mu.KLogging
import org.springframework.stereotype.Component
import java.lang.RuntimeException

@Component
class UserImportService(
    private val userRepository: UserRepository,
    private val identityProvider: IdentityProvider,
    private val organisationMatcher: OrganisationMatcher
) {

    companion object : KLogging()

    fun importFromIdentityProvider(userIds: List<UserId>) {
        val allIdentities = identityProvider.getUsers()

        allIdentities.forEach {
            userRepository.findById(it.id) ?: importFromIdentityProvider(it.id)
        }
    }

    fun importFromIdentityProvider(userId: UserId): User {
        val authenticatedUser = UserExtractor.getCurrentUser() ?: throw NotAuthenticatedException()

        if (userRepository.findById(userId) !== null) {
            throw UserAlreadyExistsException()
        }

        return identityProvider.getUserById(userId)?.let { identity ->
            val newUser = userRepository.save(
                convertIdentityToUser(identity, organisationMatcher.match(authenticatedUser)?.id)
            )

            logger.info { "Imported user $userId" }

            newUser
        } ?: throw UserNotFoundException(userId)
    }
}
