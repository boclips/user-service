package com.boclips.users.application.commands

import com.boclips.users.application.CaptchaProvider
import com.boclips.users.application.exceptions.CaptchaScoreBelowThresholdException
import com.boclips.users.domain.model.NewTeacher
import com.boclips.users.domain.model.User
import com.boclips.users.domain.model.UserSessions
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.analytics.Event
import com.boclips.users.domain.model.analytics.EventType
import com.boclips.users.domain.service.AnalyticsClient
import com.boclips.users.domain.service.MarketingService
import com.boclips.users.domain.service.UserService
import com.boclips.users.domain.service.convertUserToCrmProfile
import com.boclips.users.presentation.requests.CreateTeacherRequest
import mu.KLogging
import org.springframework.stereotype.Component

@Component
class CreateTeacherAccount(
    private val userService: UserService,
    private val marketingService: MarketingService,
    private val captchaProvider: CaptchaProvider,
    private val analyticsClient: AnalyticsClient,
    private val generateTeacherShareCode: GenerateTeacherShareCode
) {
    companion object : KLogging()

    operator fun invoke(createTeacherRequest: CreateTeacherRequest): User {
        if (!this.captchaProvider.validateCaptchaToken(createTeacherRequest.recaptchaToken!!)) {
            throw CaptchaScoreBelowThresholdException(createTeacherRequest.email!!)
        }

        val newUser = NewTeacher(
            email = createTeacherRequest.email!!,
            password = createTeacherRequest.password!!,
            analyticsId = AnalyticsId(value = createTeacherRequest.analyticsId.orEmpty()),
            referralCode = createTeacherRequest.referralCode.orEmpty(),
            shareCode = generateTeacherShareCode(),
            utmSource = createTeacherRequest.utmSource ?: "",
            utmContent = createTeacherRequest.utmContent ?: "",
            utmTerm = createTeacherRequest.utmTerm ?: "",
            utmMedium = createTeacherRequest.utmMedium ?: "",
            utmCampaign = createTeacherRequest.utmCampaign ?: ""
        )

        val createdUser = userService.createTeacher(newTeacher = newUser)

        attemptToUpdateProfile(createdUser)
        createdUser.analyticsId?.let { trackAccountCreatedEvent(it) }

        return createdUser
    }

    private fun attemptToUpdateProfile(createdUser: User) {
        try {
            convertUserToCrmProfile(createdUser, UserSessions(lastAccess = null))?.let {
                marketingService.updateProfile(listOf(it))
                marketingService.updateSubscription(it)
            }
        } catch (ex: Exception) {
            logger.info { "Failed to update contact $ex" }
        }
    }

    private fun trackAccountCreatedEvent(id: AnalyticsId?) {
        id?.let {
            analyticsClient.track(Event(eventType = EventType.ACCOUNT_CREATED, userId = id.value))
            logger.info { "Send MixPanel event ACCOUNT_CREATED for MixPanel ID $id" }
        }
    }
}
