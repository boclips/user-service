package com.boclips.users.application.commands

import com.boclips.users.api.request.CreateApiUserRequest
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.organisation.OrganisationRepository
import com.boclips.users.domain.model.user.Identity
import com.boclips.users.domain.model.user.UserId
import com.boclips.users.domain.service.user.UserCreationService
import org.springframework.stereotype.Service
import java.time.ZonedDateTime

@Service
class CreateApiUser(
    private val userCreationService: UserCreationService,
    private val organisationRepository: OrganisationRepository
) {

    operator fun invoke(userId: String, createApiUserRequest: CreateApiUserRequest) {
        userCreationService.create(
            Identity(id = UserId(userId), username = userId, createdAt = ZonedDateTime.now()),
            organisationRepository.findOrganisationById(OrganisationId(createApiUserRequest.organisationId))
        )
    }
}
