package com.boclips.users.domain.service

import com.boclips.users.application.exceptions.UserNotFoundException
import com.boclips.users.domain.model.NewTeacher
import com.boclips.users.domain.model.Profile
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.marketing.MarketingTracking
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class UserService(
    val userRepository: UserRepository,
    val organisationAccountRepository: OrganisationAccountRepository,
    val accountProvider: AccountProvider
) {
    companion object : KLogging()

    // TODO implement stream
    fun findAllTeachers(): List<User> {
        val schools = organisationAccountRepository.findSchools()

        val allTeachers = userRepository.findAll().filter {
            it.organisationAccountId == null || schools.map { it.id }.contains(
                it.organisationAccountId
            )
        }

        logger.info { "Fetched ${allTeachers.size} teacher users from database" }

        return allTeachers
    }

    fun createTeacher(newTeacher: NewTeacher): User {
        val account = accountProvider.createAccount(
            email = newTeacher.email,
            password = newTeacher.password
        )

        val user = userRepository.save(
            User(
                account = account,
                profile = null,
                analyticsId = newTeacher.analyticsId,
                referralCode = newTeacher.referralCode,
                marketingTracking = MarketingTracking(
                    utmCampaign = newTeacher.utmCampaign,
                    utmSource = newTeacher.utmSource,
                    utmMedium = newTeacher.utmMedium,
                    utmContent = newTeacher.utmContent,
                    utmTerm = newTeacher.utmTerm
                ),
                organisationAccountId = null,
                accessExpiry = null
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
