package com.boclips.users.domain.service

import com.boclips.users.domain.model.Identity
import com.boclips.users.domain.model.NewTeacher
import com.boclips.users.domain.model.TeacherPlatformAttributes
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.marketing.MarketingTracking
import com.boclips.users.infrastructure.organisation.OrganisationResolver
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class UserCreationService(
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

        val user = User(
            identity = identity,
            profile = null,
            teacherPlatformAttributes = TeacherPlatformAttributes(
                shareCode = newTeacher.shareCode,
                hasLifetimeAccess = false
            ),
            analyticsId = newTeacher.analyticsId,
            referralCode = newTeacher.referralCode,
            marketingTracking = newTeacher.marketingTracking,
            organisation = null,
            accessExpiresOn = null
        )

        return save(user)
    }

    fun create(identity: Identity): User {
        logger.info { "Creating user ${identity.id.value} with roles [${identity.roles.joinToString()}]" }

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

        return save(user)
    }

    private fun save(user: User): User {
        return userRepository.create(user).also { createdUser ->
            logger.info { "User ${createdUser.id.value} created under organisation ${createdUser.organisation?.name}" }
        }
    }
}
