package com.boclips.users.application.commands

import com.boclips.security.utils.UserExtractor
import com.boclips.security.utils.UserExtractor.currentUserHasRole
import com.boclips.users.presentation.resources.converters.UserConverter
import com.boclips.users.application.exceptions.NotAuthenticatedException
import com.boclips.users.application.exceptions.PermissionDeniedException
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.organisation.OrganisationAccount
import com.boclips.users.domain.service.OrganisationAccountRepository
import com.boclips.users.presentation.resources.UserResource
import org.springframework.stereotype.Component

@Component
class GetUser(
    private val organisationAccountRepository: OrganisationAccountRepository,
    private val userConverter: UserConverter,
    private val getOrImportUser: GetOrImportUser
) {
    operator fun invoke(requestedUserId: String): UserResource {
        val authenticatedUser = UserExtractor.getCurrentUser() ?: throw NotAuthenticatedException()
        val isOwnProfile = authenticatedUser.id == requestedUserId
        if (!(currentUserHasRole(UserRoles.VIEW_USERS) || isOwnProfile)) {
            throw PermissionDeniedException()
        }

        val userId = UserId(value = requestedUserId)

        val user = getOrImportUser(userId)
        val organisationAccount: OrganisationAccount<*>? =
            user.organisationAccountId?.let { organisationAccountRepository.findOrganisationAccountById(it) }

        return userConverter.toUserResource(user, organisationAccount)
    }
}
