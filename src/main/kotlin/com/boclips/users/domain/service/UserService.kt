package com.boclips.users.domain.service

import com.boclips.users.application.exceptions.UserNotFoundException
import com.boclips.users.domain.model.NewUser
import com.boclips.users.domain.model.UpdatedUser
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.UserSource
import com.boclips.users.domain.model.marketing.MarketingTracking
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class UserService(
    val userRepository: UserRepository,
    val identityProvider: IdentityProvider
) {
    companion object : KLogging()

    fun findTeacherById(id: UserId): User {
        val retrievedUser = userRepository.findById(UserId(id.value))
        val user = retrievedUser ?: throw UserNotFoundException(id)

        if (retrievedUser.associatedTo != UserSource.Boclips) throw UserNotFoundException(id)

        logger.info { "Fetched teacher user ${id.value}" }

        return user
    }

    fun findAllTeachers(): List<User> {
        val allUsers = userRepository.findAll().filter { it.associatedTo == UserSource.Boclips }
        logger.info { "Fetched ${allUsers.size} teacher users from database" }

        return allUsers
    }

    fun findUserById(userId: UserId): User {
        val retrievedUser = userRepository.findById(UserId(userId.value))
        return retrievedUser ?: throw UserNotFoundException(userId)
    }

    fun createTeacher(newUser: NewUser): User {
        val identity = identityProvider.createUser(
            email = newUser.email,
            password = newUser.password
        )

        val user = userRepository.save(
            User(
                id = UserId(identity.id.value),
                activated = false,
                analyticsId = newUser.analyticsId,
                subjects = emptyList(),
                ages = emptyList(),
                referralCode = newUser.referralCode,
                firstName = null,
                lastName = null,
                email = newUser.email,
                hasOptedIntoMarketing = false,
                marketingTracking = MarketingTracking(
                    utmCampaign = newUser.utmCampaign,
                    utmSource = newUser.utmSource,
                    utmMedium = newUser.utmMedium,
                    utmContent = newUser.utmContent,
                    utmTerm = newUser.utmTerm
                ),
                associatedTo = UserSource.Boclips
            )
        )

        logger.info { "Created user ${user.id.value}" }

        return user
    }

    fun updateUserDetails(updatedUser: UpdatedUser): User {
        val originalUser =
            userRepository.findById(updatedUser.userId) ?: throw UserNotFoundException(
                updatedUser.userId
            )

        val user = userRepository.save(
            originalUser.copy(
                firstName = updatedUser.firstName,
                lastName = updatedUser.lastName,
                hasOptedIntoMarketing = updatedUser.hasOptedIntoMarketing,
                subjects = updatedUser.subjects,
                ages = updatedUser.ages
            )
        )

        logger.info { "Updated user ${user.id.value}" }

        return user
    }
}
