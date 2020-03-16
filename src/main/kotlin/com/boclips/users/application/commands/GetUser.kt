package com.boclips.users.application.commands

import com.boclips.security.utils.UserExtractor
import com.boclips.security.utils.UserExtractor.currentUserHasRole
import com.boclips.users.application.exceptions.NotAuthenticatedException
import com.boclips.users.application.exceptions.PermissionDeniedException
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.service.OrganisationRepository
import com.boclips.users.presentation.resources.UserResource
import com.boclips.users.presentation.resources.converters.UserConverter
import org.springframework.stereotype.Component

@Component
class GetUser(
    private val organisationRepository: OrganisationRepository,
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
        val organisation: Organisation<*>? =
            user.organisationId?.let { organisationRepository.findOrganisationById(it) }

        return userConverter.toUserResource(user, organisation)
    }
}
