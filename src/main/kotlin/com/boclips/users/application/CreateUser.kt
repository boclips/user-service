package com.boclips.users.application

import com.boclips.users.application.exceptions.CaptchaScoreBelowThresholdException
import com.boclips.users.application.exceptions.InvalidSubjectException
import com.boclips.users.domain.model.NewUser
import com.boclips.users.domain.model.SubjectId
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserSessions
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.analytics.Event
import com.boclips.users.domain.model.analytics.EventType
import com.boclips.users.domain.service.AnalyticsClient
import com.boclips.users.domain.service.MarketingService
import com.boclips.users.domain.service.SubjectService
import com.boclips.users.domain.service.UserService
import com.boclips.users.domain.service.userToCrmProfile
import com.boclips.users.presentation.requests.CreateUserRequest
import mu.KLogging
import org.springframework.stereotype.Component

@Component
class CreateUser(
    private val userService: UserService,
    private val marketingService: MarketingService,
    private val captchaProvider: CaptchaProvider,
    private val subjectService: SubjectService,
    private val analyticsClient: AnalyticsClient
) {
    companion object : KLogging()

    operator fun invoke(createUserRequest: CreateUserRequest): User {
        if (!this.captchaProvider.validateCaptchaToken(createUserRequest.recaptchaToken!!)) {
            throw CaptchaScoreBelowThresholdException(createUserRequest.email!!)
        }

        if (invalidSubjects(createUserRequest.subjects)) {
            throw InvalidSubjectException(createUserRequest.subjects.orEmpty())
        }

        val newUser = NewUser(
            firstName = createUserRequest.firstName!!,
            lastName = createUserRequest.lastName!!,
            email = createUserRequest.email!!,
            password = createUserRequest.password!!,
            analyticsId = AnalyticsId(value = createUserRequest.analyticsId.orEmpty()),
            subjects = createUserRequest.subjects.orEmpty(),
            ageRange = createUserRequest.ageRange.orEmpty(),
            referralCode = createUserRequest.referralCode.orEmpty(),
            hasOptedIntoMarketing = createUserRequest.hasOptedIntoMarketing!!,
            utmSource = createUserRequest.utmSource ?: "",
            utmContent = createUserRequest.utmContent ?: "",
            utmTerm = createUserRequest.utmTerm ?: "",
            utmMedium = createUserRequest.utmMedium ?: "",
            utmCampaign = createUserRequest.utmCampaign ?: ""
        )

        val createdUser = userService.createUser(newUser = newUser)

        attemptToUpdateProfile(createdUser)
        trackAccountCreatedEvent(createdUser.analyticsId)

        return createdUser
    }

    private fun attemptToUpdateProfile(createdUser: User) {
        try {
            val crmProfile = userToCrmProfile(createdUser, UserSessions(lastAccess = null))
            marketingService.updateProfile(listOf(crmProfile))
            marketingService.updateSubscription(crmProfile)
        } catch (ex: Exception) {
            logger.info { "Failed to update contact $ex" }
        }
    }

    private fun invalidSubjects(subjects: List<String>?) =
        !subjectService.allSubjectsExist(subjects.orEmpty().map { SubjectId(value = it) })

    private fun trackAccountCreatedEvent(id: AnalyticsId?) {
        id?.let {
            analyticsClient.track(Event(eventType = EventType.ACCOUNT_CREATED, userId = id.value))
            logger.info { "Send MixPanel event ACCOUNT_CREATED for MixPanel ID $id" }
        }
    }
}