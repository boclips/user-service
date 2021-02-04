package com.boclips.users.domain.service.user

import com.boclips.users.application.commands.GenerateShareCode
import com.boclips.users.domain.model.marketing.MarketingTracking
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationRepository
import com.boclips.users.domain.model.user.Identity
import com.boclips.users.domain.model.user.NewTeacher
import com.boclips.users.domain.model.user.TeacherPlatformAttributes
import com.boclips.users.domain.model.user.User
import com.boclips.users.domain.model.user.UserId
import com.boclips.users.domain.model.user.UserRepository
import com.boclips.users.domain.service.organisation.resolvers.OrganisationResolver
import mu.KLogging
import org.springframework.stereotype.Service
import java.time.ZonedDateTime

@Service
class UserCreationService(
    val userRepository: UserRepository,
    val organisationRepository: OrganisationRepository,
    val identityProvider: IdentityProvider,
    val organisationResolver: OrganisationResolver,
    val generateShareCode: GenerateShareCode
) {
    companion object : KLogging() {
        const val TEACHER_ROLE = "ROLE_TEACHER"
    }

    fun create(identity: Identity): User {
        return create(
            identity = identity,
            organisation = organisationResolver.resolve(identity)
        )
    }

    fun createTeacher(newTeacher: NewTeacher): User {
        val identity = identityProvider.createIdentity(
            email = newTeacher.email,
            password = newTeacher.password,
            role = TEACHER_ROLE
        )

        return create(
            identity = identity,
            organisation = organisationResolver.resolve(identity)
        ) {
            User(
                teacherPlatformAttributes = TeacherPlatformAttributes(
                    hasLifetimeAccess = false
                ),
                analyticsId = newTeacher.analyticsId,
                referralCode = newTeacher.referralCode,
                marketingTracking = newTeacher.marketingTracking,
                accessExpiresOn = it.accessExpiresOn,
                identity = it.identity,
                profile = it.profile,
                shareCode = newTeacher.shareCode,
            )
        }
    }

    fun synchroniseIntegrationUser(externalUserId: String, deploymentOrganisation: Organisation): User {
        return userRepository.findAllByOrganisationId(deploymentOrganisation.id)
            .find { user -> user.identity.username == externalUserId }
            ?: createLtiDeploymentUser(externalUserId, deploymentOrganisation)
    }

    fun create(
        identity: Identity,
        organisation: Organisation?,
        setup: (defaults: User) -> User = { user -> user }
    ): User {
        logger.info { "Creating user ${identity.id.value} with roles [${identity.roles.joinToString()}]" }

        return User(
            identity = identity,
            profile = null,
            teacherPlatformAttributes = null,
            marketingTracking = MarketingTracking(),
            referralCode = null,
            analyticsId = null,
            organisation = organisation,
            accessExpiresOn = null,
            shareCode = generateShareCode()
        )
            .let(setup)
            .let(userRepository::create)
            .also { user ->
                logger.info { "User ${user.id.value} created under organisation ${user.organisation?.name}" }
            }
    }

    private fun createLtiDeploymentUser(externalUserId: String, organisation: Organisation): User {
        return create(
            Identity(
                id = UserId(),
                username = externalUserId,
                createdAt = ZonedDateTime.now()
            ),
            organisation
        )
    }
}
