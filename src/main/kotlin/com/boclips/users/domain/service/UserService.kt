package com.boclips.users.domain.service

import com.boclips.users.application.exceptions.UserNotFoundException
import com.boclips.users.domain.model.NewTeacher
import com.boclips.users.domain.model.TeacherPlatformAttributes
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.marketing.MarketingTracking
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class UserService(
    val userRepository: UserRepository,
    val accountRepository: AccountRepository,
    val identityProvider: IdentityProvider
) {
    companion object : KLogging()

    // TODO implement stream
    fun findAllTeachers(): List<User> {
        val schools = accountRepository.findSchools()

        val allTeachers = userRepository.findAll().filter {
            it.organisationId == null || schools.map { it.id }.contains(
                it.organisationId
            )
        }

        logger.info { "Fetched ${allTeachers.size} teacher users from database" }

        return allTeachers
    }

    fun createTeacher(newTeacher: NewTeacher): User {
        val identity = identityProvider.createIdentity(
            email = newTeacher.email,
            password = newTeacher.password
        )

        val user = userRepository.create(
            User(
                identity = identity,
                profile = null,
                teacherPlatformAttributes = TeacherPlatformAttributes(
                    shareCode = newTeacher.shareCode,
                    hasLifetimeAccess = false
                ),
                analyticsId = newTeacher.analyticsId,
                referralCode = newTeacher.referralCode,
                marketingTracking = MarketingTracking(
                    utmCampaign = newTeacher.utmCampaign,
                    utmSource = newTeacher.utmSource,
                    utmMedium = newTeacher.utmMedium,
                    utmContent = newTeacher.utmContent,
                    utmTerm = newTeacher.utmTerm
                ),
                organisationId = null,
                accessExpiresOn = null
            )
        )

        logger.info { "Created user ${user.id.value}" }

        return user
    }

    fun findUserById(userId: UserId): User {
        val retrievedUser = userRepository.findById(UserId(userId.value))

        return retrievedUser ?: throw UserNotFoundException(userId)
    }
}
