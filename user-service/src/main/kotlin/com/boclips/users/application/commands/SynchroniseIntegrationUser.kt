package com.boclips.users.application.commands

import com.boclips.security.utils.UserExtractor
import com.boclips.users.application.exceptions.NotAuthenticatedException
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.user.User
import com.boclips.users.domain.model.user.UserId
import com.boclips.users.domain.model.user.UserRepository
import com.boclips.users.domain.service.organisation.OrganisationService
import com.boclips.users.domain.service.user.UserCreationService
import org.springframework.stereotype.Component

@Component
class SynchroniseIntegrationUser(
    val userRepository: UserRepository,
    val organisationService: OrganisationService,
    val userCreationService: UserCreationService,
    val getOrImportUser: GetOrImportUser
) {
    operator fun invoke(deploymentId: String, externalUserId: String): User {
        val topLevelOrganisationId = getTopLevelOrganisationId()

        val deploymentOrganisation = organisationService.findOrCreateLtiDeployment(topLevelOrganisationId, deploymentId)
        return userCreationService.synchroniseIntegrationUser(externalUserId, deploymentOrganisation)
    }

    private fun getTopLevelOrganisationId(): OrganisationId {
        val userId = UserExtractor.getCurrentUser()?.let { UserId(it.id)} ?: throw NotAuthenticatedException()
        val integrationUser = getOrImportUser(userId)
        return integrationUser.organisation!!.id
    }
}
