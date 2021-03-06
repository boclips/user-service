package com.boclips.users.application.commands

import com.boclips.users.api.factories.CreateUserRequestFactory
import com.boclips.users.api.request.user.CreateUserRequest
import com.boclips.users.application.exceptions.CaptchaScoreBelowThresholdException
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

class CreateTeacherIntegrationTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var createTeacher: CreateTeacher

    @Test
    fun `create account without optional values`() {
        val shareCodePattern = """^[\w\d]{4}$""".toRegex()

        val createdAccount = createTeacher(
            CreateUserRequest.CreateTeacherRequest(
                email = "hans@muster.com",
                password = "hansli",
                recaptchaToken = "SOMERECAPTCHATOKENHERE"
            )
        )

        val user = userRepository.findById(createdAccount.id)
        assertThat(user).isNotNull
        assertThat(user!!.isReferral()).isFalse()
        assertThat(user.referralCode).isEmpty()
        assertTrue(
            user.shareCode!!.matches(shareCodePattern),
            "Expected ${user.shareCode!!} to be 4 characters long"
        )
        assertThat(user.analyticsId).isEqualTo(AnalyticsId(value = ""))

        val account = identityProvider.getIdentitiesById(createdAccount.id)
        assertThat(account).isNotNull
        assertThat(account!!.email).isEqualTo("hans@muster.com")
    }

    @Test
    fun `create account with referral and analytics information`() {
        val user = createTeacher(
            CreateUserRequestFactory.teacher(
                referralCode = "referral-code-123",
                analyticsId = "123",
                utmMedium = "utm-medium",
                utmSource = "facebook",
                utmTerm = "utm-term",
                utmContent = "utm-content",
                utmCampaign = "utm-campaign"
            )
        )

        val persistedAccount = userRepository.findById(user.id)!!

        assertThat(persistedAccount.identity.createdAt).isNotNull()
        assertThat(persistedAccount.isReferral()).isTrue()
        assertThat(persistedAccount.referralCode).isEqualTo("referral-code-123")
        assertThat(persistedAccount.analyticsId!!.value).isEqualTo("123")
        assertThat(persistedAccount.marketingTracking.utmSource).isEqualTo("facebook")
        assertThat(persistedAccount.marketingTracking.utmContent).isEqualTo("utm-content")
        assertThat(persistedAccount.marketingTracking.utmTerm).isEqualTo("utm-term")
        assertThat(persistedAccount.marketingTracking.utmMedium).isEqualTo("utm-medium")
        assertThat(persistedAccount.marketingTracking.utmCampaign).isEqualTo("utm-campaign")
    }

    @Test
    fun `throw an exception when the captcha verification fails`() {
        whenever(captchaProvider.validateCaptchaToken(any())).thenReturn(false)

        assertThrows<CaptchaScoreBelowThresholdException> {
            createTeacher(CreateUserRequestFactory.teacher())
        }
    }

    @Test
    fun `update contact on hubspot after user creation`() {
        createTeacher(CreateUserRequestFactory.teacher())

        verify(marketingService, times(1)).updateProfile(any())
    }

    @Test
    fun `update subscription on hubspot after user creation`() {
        createTeacher(CreateUserRequestFactory.teacher())

        verify(marketingService, times(1)).updateSubscription(any())
    }
}
