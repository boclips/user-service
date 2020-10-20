package com.boclips.users.application.commands

import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.user.User
import com.boclips.users.domain.model.user.UserRepository
import com.boclips.users.domain.model.user.UserUpdate
import com.boclips.users.domain.service.marketing.MarketingService
import com.boclips.users.domain.service.user.IdentityProvider
import mu.KLogging
import org.springframework.stereotype.Component

@Component
class DeleteOrganisationPII(
    private val userRepository: UserRepository,
    private val identityProvider: IdentityProvider,
    private val marketingService: MarketingService
) {
    companion object : KLogging()

    operator fun invoke(organisationId: String) {
        userRepository.findAllByOrganisationId(OrganisationId(organisationId)).let { allUsers ->
            val windowSize = 100
            allUsers
                .windowed(size = windowSize, step = windowSize, partialWindows = true)
                .map { usersBatch ->
                    usersBatch.forEach { user ->
                        userRepository.update(
                            user = user,
                            UserUpdate.ReplaceEmail(""),
                            UserUpdate.ReplaceFirstName(""),
                            UserUpdate.ReplaceLastName(""),
                        )
                        marketingService.deleteContact(user.id.value)
                        identityProvider.deleteIdentity(user.id)
                    }

                    //Get around hubspot rate limiting
                    Thread.sleep(10000)
                }

        }
    }
}
