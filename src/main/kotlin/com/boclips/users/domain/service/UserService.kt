package com.boclips.users.domain.service

import com.boclips.users.application.exceptions.UserNotFoundException
import com.boclips.users.domain.model.Account
import com.boclips.users.domain.model.NewTeacher
import com.boclips.users.domain.model.Profile
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.UserSource
import com.boclips.users.domain.model.marketing.MarketingTracking
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class UserService(
    val userRepository: UserRepository,
    val accountProvider: AccountProvider
) {
    companion object : KLogging()

    fun findTeacherById(id: UserId): User {
        val retrievedUser = userRepository.findById(UserId(id.value))
        val user = retrievedUser ?: throw UserNotFoundException(id)

        if (retrievedUser.account.associatedTo != UserSource.Boclips) throw UserNotFoundException(id)

        logger.info { "Fetched teacher user ${id.value}" }

        return user
    }

    // TODO implement stream
    fun findAllTeachers(): List<User> {
        val allUsers = userRepository.findAll().filter { it.account.associatedTo == UserSource.Boclips }
        logger.info { "Fetched ${allUsers.size} teacher users from database" }

        return allUsers
    }

    fun createTeacher(newTeacher: NewTeacher): User {
        val account = accountProvider.createAccount(
            email = newTeacher.email,
            password = newTeacher.password
        )

        val user = userRepository.save(
            User(
                account = Account(
                    id = UserId(account.id.value),
                    username = newTeacher.email,
                    associatedTo = UserSource.Boclips
                ),
                profile = null,
                analyticsId = newTeacher.analyticsId,
                referralCode = newTeacher.referralCode,
                marketingTracking = MarketingTracking(
                    utmCampaign = newTeacher.utmCampaign,
                    utmSource = newTeacher.utmSource,
                    utmMedium = newTeacher.utmMedium,
                    utmContent = newTeacher.utmContent,
                    utmTerm = newTeacher.utmTerm
                )
            )
        )

        logger.info { "Created user ${user.id.value}" }

        return user
    }

    fun findUserById(userId: UserId): User {
        val retrievedUser = userRepository.findById(UserId(userId.value))
        return retrievedUser ?: throw UserNotFoundException(userId)
    }

    fun updateProfile(userId: UserId, profile: Profile): User {
        val originalUser =
            userRepository.findById(userId) ?: throw UserNotFoundException(userId)

        val user = userRepository.save(
            originalUser.copy(
                profile = Profile(
                    firstName = profile.firstName,
                    lastName = profile.lastName,
                    hasOptedIntoMarketing = profile.hasOptedIntoMarketing,
                    subjects = profile.subjects,
                    ages = profile.ages
                )
            )
        )

        logger.info { "Updated user ${user.id.value}" }

        return user
    }
}
