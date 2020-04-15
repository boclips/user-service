package com.boclips.users.domain.service

import com.boclips.eventbus.BoclipsEventListener
import com.boclips.eventbus.events.organisation.OrganisationUpdated
import com.boclips.users.application.exceptions.UserNotFoundException
import com.boclips.users.domain.model.Identity
import com.boclips.users.domain.model.NewTeacher
import com.boclips.users.domain.model.TeacherPlatformAttributes
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.marketing.MarketingTracking
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.infrastructure.organisation.OrganisationResolver
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class UserService(
    val userRepository: UserRepository,
    val organisationRepository: OrganisationRepository,
    val identityProvider: IdentityProvider,
    val organisationResolver: OrganisationResolver
) {
    companion object : KLogging()

    // TODO implement stream
    fun findAllTeachers(): List<User> {
        val schools = organisationRepository.findSchools()

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
        logger.info { "Retrieved user ${userId.value}" }
        return retrievedUser ?: throw UserNotFoundException(userId)
    }

    fun create(identity: Identity): User {
        val organisation = organisationResolver.resolve(identity.roles)

        val user = User(
            identity = identity,
            profile = null,
            teacherPlatformAttributes = null,
            marketingTracking = MarketingTracking(),
            referralCode = null,
            analyticsId = null,
            organisation = organisation,
            organisationId = organisation?.id,
            accessExpiresOn = null
        )

        return userRepository.create(user)
    }

    @BoclipsEventListener
    fun updateOrganisation(organisationUpdated: OrganisationUpdated) {
        val organisation =
            organisationRepository.findOrganisationById(OrganisationId(organisationUpdated.organisation.id))!!

        userRepository.findAllByOrganisationId(organisation.id).forEach { user ->
            userRepository.update(user, UserUpdateCommand.ReplaceOrganisation(organisation))
        }
    }
}
