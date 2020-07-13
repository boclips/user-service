package com.boclips.users.application.commands

import com.boclips.users.api.request.user.CreateE2EUserRequest
import com.boclips.users.application.exceptions.NotFoundException
import com.boclips.users.domain.model.marketing.MarketingTracking
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.organisation.OrganisationRepository
import com.boclips.users.domain.model.user.User
import com.boclips.users.domain.model.user.UserRepository
import com.boclips.users.domain.service.user.IdentityProvider
import com.boclips.users.presentation.annotations.BoclipsE2ETestSupport
import org.springframework.stereotype.Component

@Component
@BoclipsE2ETestSupport
class CreateE2EUser(
    private val organisationRepository: OrganisationRepository,
    private val identityProvider: IdentityProvider,
    private val userRepository: UserRepository
) {
    operator fun invoke(createE2EUserRequest: CreateE2EUserRequest): User {
        val organisationId = OrganisationId(createE2EUserRequest.organisationId!!)

        if (!organisationId.isValid()) {
            throw NotFoundException("Cannot find organisation: $organisationId")
        }

        val organisation = organisationRepository.findOrganisationById(organisationId)
            ?: throw NotFoundException("Cannot find organisation: $organisationId")

        val identity = identityProvider.createIdentity(
            email = createE2EUserRequest.email!!,
            password = createE2EUserRequest.password!!,
            role = "ROLE_E2E"
        )

        return userRepository.create(
            User(
                identity = identity,
                organisation = organisation,
                marketingTracking = MarketingTracking(),
                profile = null,
                accessExpiresOn = null,
                referralCode = null,
                teacherPlatformAttributes = null
            )
        )
    }
}
