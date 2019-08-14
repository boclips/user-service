package com.boclips.users.domain.service

import com.boclips.users.domain.model.NewUser
import com.boclips.users.domain.model.UpdatedUser
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.UserNotFoundException
import com.boclips.users.domain.model.marketing.MarketingTracking
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class TeachersPlatformService(
    val userRepository: UserRepository,
    val identityProvider: IdentityProvider,
    val organisationRepository: OrganisationRepository
) {
    companion object : KLogging()

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
                ages = newUser.ageRange,
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
                ),
                associatedTo = null
            )
        )

        logger.info { "Created user ${user.id.value}" }

        return user
    }

    fun updateUserDetails(updatedUser: UpdatedUser): User {
        val originalUser = userRepository.findById(updatedUser.userId) ?: throw UserNotFoundException(updatedUser.userId)

        val user = userRepository.save(originalUser.copy(
            firstName = updatedUser.firstName,
            lastName = updatedUser.lastName,
            hasOptedIntoMarketing = updatedUser.hasOptedIntoMarketing,
            subjects = updatedUser.subjects,
            ages = updatedUser.ages
        ))

        logger.info { "Updated user ${user.id.value}" }

        return user
    }
}
