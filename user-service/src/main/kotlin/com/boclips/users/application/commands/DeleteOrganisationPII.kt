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
            logger.info { "found ${allUsers.size} users for organisation $organisationId" }
            val windowSize = 50
            allUsers
                .windowed(size = windowSize, step = windowSize, partialWindows = true)
                .map { usersBatch ->
                    logger.info { "got batch of ${usersBatch.size} users" }
                    usersBatch.forEach { user: User ->
                        logger.info { "CURRENT USER:${user.id.value}" }
                        user.getContactDetails()?.email?.let {
                            marketingService.deleteContact(it)
                        }
                        logger.info { "wiping email:${user.identity.email}, firstname:${user.profile?.firstName}, lastname:${user.profile?.lastName}" }
                        userRepository.update(
                            user = user,
                            // UserUpdate.ReplaceEmail(""),
                            UserUpdate.ReplaceFirstName(""),
                            UserUpdate.ReplaceLastName(""),
                        )
                        logger.info { "now deleting idp ${user.id.value}" }
                        identityProvider.deleteIdentity(user.id)
                    }
                    //Get around hubspot rate limiting
                    logger.info { "sleep 10s" }
                    Thread.sleep(10000)
                }

        }
    }
}
