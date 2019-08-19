package com.boclips.users.application

import com.boclips.eventbus.EventBus
import com.boclips.eventbus.events.user.UserActivated
import com.boclips.security.utils.UserExtractor
import com.boclips.users.application.exceptions.InvalidSubjectException
import com.boclips.users.application.exceptions.NotAuthenticatedException
import com.boclips.users.application.exceptions.PermissionDeniedException
import com.boclips.users.domain.model.Profile
import com.boclips.users.domain.model.Subject
import com.boclips.users.domain.model.SubjectId
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.UserSessions
import com.boclips.users.domain.model.UserSource
import com.boclips.users.domain.model.referrals.NewReferral
import com.boclips.users.domain.service.MarketingService
import com.boclips.users.domain.service.ReferralProvider
import com.boclips.users.domain.service.SubjectService
import com.boclips.users.domain.service.UserRepository
import com.boclips.users.domain.service.UserService
import com.boclips.users.domain.service.convertUserToCrmProfile
import com.boclips.users.presentation.requests.UpdateUserRequest
import mu.KLogging
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class UpdateUser(
    private val userService: UserService,
    private val userRepository: UserRepository,
    private val referralProvider: ReferralProvider,
    private val marketingService: MarketingService,
    private val subjectService: SubjectService,
    private val eventBus: EventBus
) {
    companion object : KLogging()

    operator fun invoke(userId: String, updateUserRequest: UpdateUserRequest): User {
        val authenticatedUser = UserExtractor.getCurrentUser() ?: throw NotAuthenticatedException()
        if (authenticatedUser.id != userId) throw PermissionDeniedException()

        val user = userService.findUserById(UserId(authenticatedUser.id))

        updateUserRequest.run {
            userService.updateProfile(
                userId = user.id,
                profile = Profile(
                    firstName = this.firstName!!,
                    lastName = this.lastName!!,
                    subjects = convertSubjects(this),
                    ages = this.ages.orEmpty(),
                    hasOptedIntoMarketing = this.hasOptedIntoMarketing!!
                )
            )
        }

        if (!user.hasProfile()) activate(UserId(authenticatedUser.id))

        return userService.findUserById(UserId(authenticatedUser.id))
    }

    private fun activate(id: UserId) {
        val user = userService.findUserById(id)

        if (user.isReferral()) {
            registerReferral(user)
        }

        convertUserToCrmProfile(user, UserSessions(Instant.now()))?.let {
            marketingService.updateProfile(listOf(it))
        }

        publishUserActivated(user)

        logger.info { "Activated user $user" }
    }

    private fun publishUserActivated(user: User) {
        val count = userRepository.count()
        eventBus.publish(
            UserActivated.builder()
                .user(
                    com.boclips.eventbus.domain.user.User.builder()
                        .id(user.id.value)
                        .organisationId(
                            when (user.account.associatedTo) {
                                is UserSource.Boclips -> null
                                is UserSource.ApiClient -> user.account.associatedTo.organisationId.value
                            }
                        )
                        .isBoclipsEmployee(user.account.isBoclipsEmployee())
                        .build()
                )
                .totalUsers(count.total)
                .activatedUsers(count.activated)
                .build()
        )
    }

    private fun registerReferral(activatedUser: User) {
        if (activatedUser.referralCode.isNullOrBlank()) {
            return
        }

        activatedUser.runIfHasContactDetails {
            referralProvider.createReferral(
                NewReferral(
                    referralCode = activatedUser.referralCode,
                    firstName = it.firstName,
                    lastName = it.lastName,
                    email = it.email,
                    externalIdentifier = activatedUser.id.value,
                    status = "qualified"
                )
            )
            logger.info { "Confirmed referral of user ${activatedUser.id}" }
        }
    }

    private fun convertSubjects(updateUserRequest: UpdateUserRequest): List<Subject> {
        return if (containsInvalidSubjects(updateUserRequest.subjects)) {
            throw InvalidSubjectException(updateUserRequest.subjects.orEmpty())
        } else {
            subjectService.getSubjectsById(updateUserRequest.subjects.orEmpty().map { SubjectId(value = it) })
        }
    }

    private fun containsInvalidSubjects(subjects: List<String>?) =
        !subjectService.allSubjectsExist(subjects.orEmpty().map { SubjectId(value = it) })
}
