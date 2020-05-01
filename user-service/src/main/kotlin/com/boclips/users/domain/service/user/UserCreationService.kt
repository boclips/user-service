package com.boclips.users.domain.service.user

import com.boclips.users.domain.model.user.Identity
import com.boclips.users.domain.model.user.NewTeacher
import com.boclips.users.domain.model.user.TeacherPlatformAttributes
import com.boclips.users.domain.model.user.User
import com.boclips.users.domain.model.marketing.MarketingTracking
import com.boclips.users.domain.model.organisation.OrganisationRepository
import com.boclips.users.domain.model.user.UserRepository
import com.boclips.users.domain.service.organisation.OrganisationResolver
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

    fun create(identity: Identity): User {
        return create(identity) { it }
    }

    fun createTeacher(newTeacher: NewTeacher): User {
        val identity = identityProvider.createIdentity(
            email = newTeacher.email,
            password = newTeacher.password,
            role = TEACHER_ROLE
        )

        return create(identity) {
            it.copy(
                teacherPlatformAttributes = TeacherPlatformAttributes(
                    shareCode = newTeacher.shareCode,
                    hasLifetimeAccess = false
                ),
                analyticsId = newTeacher.analyticsId,
                referralCode = newTeacher.referralCode,
                marketingTracking = newTeacher.marketingTracking
            )
        }
    }

    private fun create(identity: Identity, setup: (defaults: User) -> User): User {
        logger.info { "Creating user ${identity.id.value} with roles [${identity.roles.joinToString()}]" }

        val organisation = organisationResolver.resolve(identity)
        val user = setup(
            User(
                identity = identity,
                profile = null,
                teacherPlatformAttributes = null,
                marketingTracking = MarketingTracking(),
                referralCode = null,
                analyticsId = null,
                organisation = organisation,
                accessExpiresOn = null
            )
        )

        return userRepository.create(user).also { createdUser ->
            logger.info { "User ${createdUser.id.value} created under organisation ${createdUser.organisation?.name}" }
        }
    }
}
