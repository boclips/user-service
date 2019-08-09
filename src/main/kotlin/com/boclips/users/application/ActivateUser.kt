package com.boclips.users.application

import com.boclips.eventbus.EventBus
import com.boclips.eventbus.events.user.UserActivated
import com.boclips.security.utils.UserExtractor
import com.boclips.users.application.exceptions.NotAuthenticatedException
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.UserSessions
import com.boclips.users.domain.model.referrals.NewReferral
import com.boclips.users.domain.service.MarketingService
import com.boclips.users.domain.service.ReferralProvider
import com.boclips.users.domain.service.UserRepository
import com.boclips.users.domain.service.UserService
import com.boclips.users.domain.service.userToCrmProfile
import mu.KLogging
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class ActivateUser(
    private val userService: UserService,
    private val userRepository: UserRepository,
    private val referralProvider: ReferralProvider,
    private val marketingService: MarketingService,
    private val eventBus: EventBus
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
        marketingService.updateProfile(listOf(crmProfile))

        publishUserActivated(activatedUser)

        logger.info { "Activated user $activatedUser" }

        return activatedUser
    }

    private fun publishUserActivated(user: User) {
        val count = userRepository.count()
        eventBus.publish(
            UserActivated.builder()
                .user(
                    com.boclips.eventbus.domain.user.User.builder()
                        .id(user.id.value)
                        .organisationId(user.organisationId?.value)
                        .isBoclipsEmployee(user.email.endsWith("@boclips.com"))
                        .build()
                )
                .totalUsers(count.total)
                .activatedUsers(count.activated)
                .build()
        )
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
