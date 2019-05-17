package com.boclips.users.application

import com.boclips.events.config.Topics
import com.boclips.events.types.UserActivated
import com.boclips.security.utils.UserExtractor
import com.boclips.users.application.exceptions.NotAuthenticatedException
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.UserSessions
import com.boclips.users.domain.model.referrals.NewReferral
import com.boclips.users.domain.service.CustomerManagementProvider
import com.boclips.users.domain.service.ReferralProvider
import com.boclips.users.domain.service.UserRepository
import com.boclips.users.domain.service.UserService
import com.boclips.users.domain.service.userToCrmProfile
import mu.KLogging
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class ActivateUser(
    private val userService: UserService,
    private val userRepository: UserRepository,
    private val referralProvider: ReferralProvider,
    private val customerManagementProvider: CustomerManagementProvider,
    private val topics: Topics
) {
    companion object : KLogging()

    operator fun invoke(): User {
        val authenticatedUser: com.boclips.security.utils.User =
            UserExtractor.getCurrentUser() ?: throw NotAuthenticatedException()

        val activatedUser = userService.activate(UserId(value = authenticatedUser.id))

        if (activatedUser.isReferral()) {
            registerReferral(activatedUser)
        }

        val crmProfile = userToCrmProfile(activatedUser, UserSessions(Instant.now()))
        customerManagementProvider.update(listOf(crmProfile))

        publishUserActivated(activatedUser)

        logger.info { "Activated user $activatedUser" }

        return activatedUser
    }

    private fun publishUserActivated(user: User) {
        val count = userRepository.count()
        topics.userActivated().send(MessageBuilder
                .withPayload(
                        UserActivated.builder()
                                .user(com.boclips.events.types.User.builder()
                                        .id(user.id.value)
                                        .email(user.email)
                                        .build()
                                )
                                .totalUsers(count.total)
                                .activatedUsers(count.activated)
                                .build())
                .build())
    }

    private fun registerReferral(activatedUser: User) {
        val referral = NewReferral(
            referralCode = activatedUser.referralCode!!,
            firstName = activatedUser.firstName,
            lastName = activatedUser.lastName,
            email = activatedUser.email,
            externalIdentifier = activatedUser.id.value,
            status = "qualified"
        )

        referralProvider.createReferral(referral)
        logger.info { "Confirmed referral of user ${activatedUser.id}" }
    }
}
