package com.boclips.users.application.commands

import com.boclips.security.utils.UserExtractor
import com.boclips.security.utils.UserExtractor.currentUserHasRole
import com.boclips.users.application.UserImportService
import com.boclips.users.application.converters.UserConverter
import com.boclips.users.application.exceptions.NotAuthenticatedException
import com.boclips.users.application.exceptions.PermissionDeniedException
import com.boclips.users.application.exceptions.UserNotFoundException
import com.boclips.users.config.security.UserRoles
import com.boclips.users.application.exceptions.AccountNotFoundException
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationAccount
import com.boclips.users.domain.model.organisation.School
import com.boclips.users.domain.service.OrganisationAccountRepository
import com.boclips.users.domain.service.UserRepository
import com.boclips.users.presentation.resources.UserResource
import org.springframework.stereotype.Component

@Component
class GetUser(
    private val userRepository: UserRepository,
    private val userImportService: UserImportService,
    private val organisationAccountRepository: OrganisationAccountRepository,
    private val userConverter: UserConverter
) {
    operator fun invoke(requestedUserId: String): UserResource {
        val authenticatedUser = UserExtractor.getCurrentUser() ?: throw NotAuthenticatedException()
        val isOwnProfile = authenticatedUser.id == requestedUserId
        if (!(currentUserHasRole(UserRoles.VIEW_USERS) || isOwnProfile)) {
            throw PermissionDeniedException()
        }

        val userId = UserId(value = requestedUserId)

        val user = userRepository.findById(id = userId) ?: importUser(userId)
        val organisationAccount: OrganisationAccount<*>? =
            user.organisationAccountId?.let { organisationAccountRepository.findOrganisationAccountById(it) }

        return userConverter.toUserResource(user, organisationAccount)
    }

    private fun importUser(userId: UserId): User {
        return try {
            userImportService.importFromAccountProvider(userId = userId)
        } catch (ex: AccountNotFoundException) {
            throw UserNotFoundException(userId)
        }
    }
}
