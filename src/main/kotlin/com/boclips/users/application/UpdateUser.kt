package com.boclips.users.application

import com.boclips.eventbus.EventBus
import com.boclips.eventbus.events.user.UserActivated
import com.boclips.security.utils.UserExtractor
import com.boclips.users.application.exceptions.InvalidSubjectException
import com.boclips.users.application.exceptions.NotAuthenticatedException
import com.boclips.users.application.exceptions.PermissionDeniedException
import com.boclips.users.domain.model.Subject
import com.boclips.users.domain.model.SubjectId
import com.boclips.users.domain.model.UpdatedUser
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.UserSessions
import com.boclips.users.domain.model.UserSource
import com.boclips.users.domain.model.referrals.NewReferral
import com.boclips.users.domain.service.MarketingService
import com.boclips.users.domain.service.ReferralProvider
import com.boclips.users.domain.service.SubjectService
import com.boclips.users.domain.service.TeachersPlatformService
import com.boclips.users.domain.service.UserRepository
import com.boclips.users.domain.service.convertUserToCrmProfile
import com.boclips.users.presentation.requests.UpdateUserRequest
import mu.KLogging
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class UpdateUser(
    private val teachersPlatformService: TeachersPlatformService,
    private val userRepository: UserRepository,
    private val referralProvider: ReferralProvider,
    private val marketingService: MarketingService,
    private val subjectService: SubjectService,
    private val eventBus: EventBus
) {
    companion object : KLogging()

    operator fun invoke(userId: String, updateUserRequest: UpdateUserRequest? = null): User {
        val authenticatedUser = UserExtractor.getCurrentUser() ?: throw NotAuthenticatedException()
        if (authenticatedUser.id != userId) throw PermissionDeniedException()

        val user = teachersPlatformService.findTeacherById(UserId(authenticatedUser.id))

        updateUserRequest?.run {
            teachersPlatformService.updateUserDetails(UpdatedUser(
                userId = user.id,
                firstName = this.firstName!!,
                lastName = this.lastName!!,
                subjects = convertSubjects(this),
                ages = this.ages.orEmpty(),
                hasOptedIntoMarketing = this.hasOptedIntoMarketing!!
            ))
        }

        if(!user.activated) activate(user)

        return teachersPlatformService.findTeacherById(UserId(authenticatedUser.id))
    }

    private fun activate(user: User) {
        userRepository.activate(UserId(value = user.id.value))

        if (user.isReferral()) {
            registerReferral(user)
        }

        val crmProfile = convertUserToCrmProfile(user, UserSessions(Instant.now()))
        marketingService.updateProfile(listOf(crmProfile))

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
                        .organisationId(when (user.associatedTo) {
                            is UserSource.Boclips -> null
                            is UserSource.ApiClient -> user.associatedTo.organisationId.value
                        })
                        .isBoclipsEmployee(user.email.endsWith("@boclips.com"))
                        .build()
                )
                .totalUsers(count.total)
                .activatedUsers(count.activated)
                .build()
        )
    }

    private fun registerReferral(activatedUser: User) {
        val referral = NewReferral(
            referralCode = activatedUser.referralCode!!,
            firstName = activatedUser.firstName,
            lastName = activatedUser.lastName,
            email = activatedUser.email,
            externalIdentifier = activatedUser.id.value,
            status = "qualified"
        )

        referralProvider.createReferral(referral)
        logger.info { "Confirmed referral of user ${activatedUser.id}" }
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
