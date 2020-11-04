package com.boclips.users.application.commands

import com.boclips.users.api.request.CreateApiUserRequest
import com.boclips.users.application.exceptions.AlreadyExistsException
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.organisation.OrganisationRepository
import com.boclips.users.domain.model.user.Identity
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

    operator fun invoke(userId: String, createApiUserRequest: CreateApiUserRequest) {
        try {
            userCreationService.create(
                Identity(id = UserId(userId), username = userId, createdAt = ZonedDateTime.now()),
                organisationRepository.findOrganisationById(OrganisationId(createApiUserRequest.organisationId))
            )
        } catch (e: UserAlreadyExistsException) {
            logger.info { "Could not create user. User: $userId already exists" }
            throw AlreadyExistsException("User: $userId already exists")
        }
    }
}
