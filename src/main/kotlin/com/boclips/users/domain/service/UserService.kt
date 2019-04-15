package com.boclips.users.domain.service

import com.boclips.users.application.CreateUser
import com.boclips.users.domain.model.NewUser
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.UserNotFoundException
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.analytics.Event
import com.boclips.users.domain.model.analytics.EventType
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class UserService(
    val userRepository: UserRepository,
    val identityProvider: IdentityProvider,
    val analyticsClient: AnalyticsClient
) {
    companion object : KLogging()

    fun activate(userId: UserId): User {
        val user = userRepository.activate(userId)!!
        return findById(id = user.id)
    }

    fun findById(id: UserId): User {
        val user = userRepository.findById(UserId(id.value)) ?: throw UserNotFoundException(id)

        logger.info { "Fetched user ${id.value}" }

        return user
    }

    fun findAllUsers(): List<User> {
        val allUsers = userRepository.findAll()
        logger.info { "Fetched ${allUsers.size} users from database" }

        return allUsers
    }

    fun createUser(newUser: NewUser): User {
        val identity = identityProvider.createUser(
            firstName = newUser.firstName,
            lastName = newUser.lastName,
            email = newUser.email,
            password = newUser.password
        )

        val user = userRepository.save(
            User(
                id = UserId(identity.id.value),
                activated = false,
                analyticsId = newUser.analyticsId,
                subjects = newUser.subjects,
                ageRange = newUser.ageRange,
                referralCode = newUser.referralCode,
                firstName = newUser.firstName,
                lastName = newUser.lastName,
                email = newUser.email,
                hasOptedIntoMarketing = newUser.hasOptedIntoMarketing
            )
        )

        CreateUser.logger.info { "Created user ${user.id.value}" }

        trackAccountCreatedEvent(newUser.analyticsId)

        return user
    }

    private fun trackAccountCreatedEvent(id: AnalyticsId) {
        if (id.value.isNotEmpty()) {
            analyticsClient.track(
                Event(
                    eventType = EventType.ACCOUNT_CREATED,
                    userId = id.value
                )
            )
            logger.info { "Send MixPanel event ACCOUNT_CREATED for MixPanel ID $id" }
        }
    }
}