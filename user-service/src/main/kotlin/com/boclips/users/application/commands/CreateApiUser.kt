package com.boclips.users.application.commands

import com.boclips.users.api.request.user.CreateUserRequest
import com.boclips.users.application.exceptions.ApiUserAlreadyExistsException
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.organisation.OrganisationRepository
import com.boclips.users.domain.model.user.ExternalIdentity
import com.boclips.users.domain.model.user.ExternalUserId
import com.boclips.users.domain.model.user.Identity
import com.boclips.users.domain.model.user.User
import com.boclips.users.domain.model.user.UserId
import com.boclips.users.domain.service.user.UserCreationService
import com.boclips.users.infrastructure.keycloak.UserAlreadyExistsException
import mu.KLogging
import org.springframework.stereotype.Service
import java.time.ZonedDateTime

@Service
class CreateApiUser(
    private val userCreationService: UserCreationService,
    private val organisationRepository: OrganisationRepository
) {
    companion object : KLogging()

    operator fun invoke(createApiUserRequest: CreateUserRequest.CreateApiUserRequest): User {
        try {
            return userCreationService.create(
                identity = Identity(
                    id = UserId(createApiUserRequest.apiUserId),
                    username = createApiUserRequest.apiUserId,
                    createdAt = ZonedDateTime.now()
                ),
                externalIdentity = ExternalIdentity(id = ExternalUserId(createApiUserRequest.externalUserId)),
                organisation = organisationRepository.findOrganisationById(OrganisationId(createApiUserRequest.organisationId))
            )
        } catch (e: UserAlreadyExistsException) {
            logger.info { "Could not create user. User: ${createApiUserRequest.apiUserId} already exists" }
            throw ApiUserAlreadyExistsException("User: ${createApiUserRequest.apiUserId} already exists")
        }
    }
}
