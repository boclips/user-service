package com.boclips.users.domain.service

import com.boclips.users.domain.model.marketing.MarketingTracking
import com.boclips.users.domain.model.NewUser
import com.boclips.users.domain.model.SubjectId
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.UserNotFoundException
import com.boclips.users.infrastructure.subjects.VideoServiceSubjectsClient
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class UserService(
    val userRepository: UserRepository,
    val subjectService: VideoServiceSubjectsClient,
    val identityProvider: IdentityProvider
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
                subjects = subjectService.getSubjectsById(newUser.subjects.map { SubjectId(value = it) }),
                ageRange = newUser.ageRange,
                referralCode = newUser.referralCode,
                firstName = newUser.firstName,
                lastName = newUser.lastName,
                email = newUser.email,
                hasOptedIntoMarketing = newUser.hasOptedIntoMarketing,
                marketingTracking = MarketingTracking(
                    utmCampaign = newUser.utmCampaign,
                    utmSource = newUser.utmSource,
                    utmMedium = newUser.utmMedium,
                    utmContent = newUser.utmContent,
                    utmTerm = newUser.utmTerm
                )
            )
        )

        logger.info { "Created user ${user.id.value}" }

        return user
    }
}
