package com.boclips.users.application.commands

import com.boclips.users.api.request.user.CreateTeacherRequest
import com.boclips.users.application.CaptchaProvider
import com.boclips.users.application.exceptions.CaptchaScoreBelowThresholdException
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.marketing.MarketingTracking
import com.boclips.users.domain.model.user.NewTeacher
import com.boclips.users.domain.model.user.User
import com.boclips.users.domain.model.user.UserSessions
import com.boclips.users.domain.service.marketing.MarketingService
import com.boclips.users.domain.service.marketing.convertUserToCrmProfile
import com.boclips.users.domain.service.user.UserCreationService
import mu.KLogging
import org.springframework.stereotype.Component

@Component
class CreateTeacher(
    private val userCreationService: UserCreationService,
    private val marketingService: MarketingService,
    private val captchaProvider: CaptchaProvider,
    private val generateShareCode: GenerateShareCode
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
            shareCode = generateShareCode(),
            marketingTracking = MarketingTracking(
                utmSource = createTeacherRequest.utmSource ?: "",
                utmContent = createTeacherRequest.utmContent ?: "",
                utmTerm = createTeacherRequest.utmTerm ?: "",
                utmMedium = createTeacherRequest.utmMedium ?: "",
                utmCampaign = createTeacherRequest.utmCampaign ?: ""
            )
        )

        val createdUser = userCreationService.createTeacher(newTeacher = newUser)

        attemptToUpdateProfile(createdUser)

        return createdUser
    }

    private fun attemptToUpdateProfile(createdUser: User) {
        try {
            convertUserToCrmProfile(
                createdUser,
                UserSessions(lastAccess = null)
            )?.let {
                marketingService.updateProfile(listOf(it))
                marketingService.updateSubscription(it)
            }
        } catch (ex: Exception) {
            logger.info { "Failed to update contact $ex" }
        }
    }
}
