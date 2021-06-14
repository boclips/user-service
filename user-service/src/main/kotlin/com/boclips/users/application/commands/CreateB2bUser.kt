package com.boclips.users.application.commands

import com.boclips.users.api.request.user.CreateUserRequest
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.organisation.OrganisationRepository
import com.boclips.users.domain.model.user.User
import com.boclips.users.domain.service.user.IdentityProvider
import com.boclips.users.domain.service.user.UserCreationService
import org.springframework.stereotype.Component

@Component
class CreateB2bUser(
    private val userCreationService: UserCreationService,
    private val organisationRepository: OrganisationRepository,
    val identityProvider: IdentityProvider,
) {

    operator fun invoke(request: CreateUserRequest.CreateB2bUserRequest): User {
        val identity = identityProvider.createIdentity(
            email = request.email,
            role = request.role,
            password = request.password,
            isPasswordTemporary = true
        )
        val organisation = organisationRepository.findOrganisationById(OrganisationId(request.organisationId))

        return userCreationService.create(
            identity = identity,
            organisation = organisation
        )
    }
}