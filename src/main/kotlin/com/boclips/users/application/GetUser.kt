package com.boclips.users.application

import com.boclips.security.utils.UserExtractor
import com.boclips.users.application.exceptions.NotAuthenticatedException
import com.boclips.users.application.exceptions.PermissionDeniedException
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.UserNotFoundException
import com.boclips.users.domain.model.marketing.MarketingTracking
import com.boclips.users.domain.service.IdentityProvider
import com.boclips.users.domain.service.UserRepository
import com.boclips.users.presentation.resources.UserConverter
import com.boclips.users.presentation.resources.UserResource
import org.springframework.stereotype.Component

@Component
class GetUser(
    private val userRepository: UserRepository,
    private val userConverter: UserConverter,
    private val identityProvider: IdentityProvider,
    private val organisationMatcher: OrganisationMatcher
) {
    operator fun invoke(requestedUserId: String): UserResource {
        val authenticatedUser = UserExtractor.getCurrentUser() ?: throw NotAuthenticatedException()

        if (authenticatedUser.id != requestedUserId) throw PermissionDeniedException()

        val userId = UserId(value = requestedUserId)
        val user = userRepository.findById(id = userId) ?: identityProvider.getUserById(
            userId
        )?.let {
            val newUser = User(
                id = userId,
                activated = false,
                analyticsId = null,
                subjects = emptyList(),
                ageRange = emptyList(),
                referralCode = null,
                firstName = it.firstName,
                lastName = it.lastName,
                email = it.email,
                hasOptedIntoMarketing = false,
                marketingTracking = MarketingTracking(
                    utmCampaign = "",
                    utmSource = "",
                    utmContent = "",
                    utmMedium = "",
                    utmTerm = ""
                ),
                organisationId = organisationMatcher.match(authenticatedUser)?.id
            )

            userRepository.save(newUser)
        } ?: throw UserNotFoundException(userId)

        return userConverter.toUserResource(user)
    }
}
