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
    companion object : KLogging() {
        const val TEACHER_ROLE = "ROLE_TEACHER"
    }

    fun createTeacher(newTeacher: NewTeacher): User {
        val identity = identityProvider.createIdentity(
            email = newTeacher.email,
            password = newTeacher.password,
            role = TEACHER_ROLE
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
                organisation = null,
                accessExpiresOn = null
            )
        )

        logger.info { "Created teacher user ${user.id.value}" }

        return user
    }

    fun findUserById(userId: UserId): User {
        val retrievedUser = userRepository.findById(UserId(userId.value))
        logger.info { "Retrieved user ${userId.value}" }
        return retrievedUser ?: throw UserNotFoundException(userId)
    }

    fun create(identity: Identity): User {
        logger.info { "Creating user ${identity.id.value} with roles [${identity.roles.joinToString()}]"  }

        val organisation = organisationResolver.resolve(identity.roles)

        val user = User(
            identity = identity,
            profile = null,
            teacherPlatformAttributes = null,
            marketingTracking = MarketingTracking(),
            referralCode = null,
            analyticsId = null,
            organisation = organisation,
            accessExpiresOn = null
        )

        return userRepository.create(user).also { createdUser ->
            logger.info { "User ${createdUser.id.value} created under organisation ${createdUser.organisation?.name}" }
        }
    }

    @BoclipsEventListener
    fun updateOrganisation(organisationUpdated: OrganisationUpdated) {
        val organisation =
            organisationRepository.findOrganisationById(OrganisationId(organisationUpdated.organisation.id))!!

        userRepository.findAllByOrganisationId(organisation.id).forEach { user ->
            userRepository.update(user, UserUpdate.ReplaceOrganisation(organisation))
        }
    }
}
