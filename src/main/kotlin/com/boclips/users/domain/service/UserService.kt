package com.boclips.users.domain.service

import com.boclips.users.application.exceptions.UserNotFoundException
import com.boclips.users.domain.model.NewTeacher
import com.boclips.users.domain.model.PLATFORM_CLOSURE_DATE
import com.boclips.users.domain.model.Profile
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.marketing.MarketingTracking
import mu.KLogging
import org.springframework.stereotype.Service
import java.time.ZonedDateTime

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

        val user = userRepository.create(
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
                accessExpiresOn = null,
                hasLifetimeAccess = account.createdAt?.isBefore(ZonedDateTime.parse(PLATFORM_CLOSURE_DATE)) ?: false
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
