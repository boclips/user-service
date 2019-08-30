package com.boclips.users.application.commands

import com.boclips.users.application.exceptions.CaptchaScoreBelowThresholdException
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.presentation.requests.CreateTeacherRequest
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.CreateUserRequestFactory
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

class CreateTeacherAccountIntegrationTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var createTeacherAccount: CreateTeacherAccount

    @Test
    fun `create account without optional values`() {
        whenever(subjectService.getSubjectsById(any())).thenReturn(
            emptyList()
        )

        val createdAccount = createTeacherAccount(
            CreateTeacherRequest(
                email = "hans@muster.com",
                password = "hansli",
                recaptchaToken = "SOMERECAPTCHATOKENHERE"
            )
        )

        val user = userRepository.findById(createdAccount.id)
        Assertions.assertThat(user).isNotNull
        Assertions.assertThat(user!!.isReferral()).isFalse()
        Assertions.assertThat(user.referralCode).isEmpty()
        Assertions.assertThat(user.analyticsId).isEqualTo(AnalyticsId(value = ""))

        val account = accountProvider.getAccountById(createdAccount.id)
        Assertions.assertThat(account).isNotNull
        Assertions.assertThat(account!!.email).isEqualTo("hans@muster.com")
    }

    @Test
    fun `create account with referral and analytics information`() {
        val user = createTeacherAccount(
            CreateUserRequestFactory.sample(
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

        Assertions.assertThat(persistedAccount.isReferral()).isTrue()
        Assertions.assertThat(persistedAccount.referralCode).isEqualTo("referral-code-123")
        Assertions.assertThat(persistedAccount.analyticsId!!.value).isEqualTo("123")
        Assertions.assertThat(persistedAccount.marketingTracking.utmSource).isEqualTo("facebook")
        Assertions.assertThat(persistedAccount.marketingTracking.utmContent).isEqualTo("utm-content")
        Assertions.assertThat(persistedAccount.marketingTracking.utmTerm).isEqualTo("utm-term")
        Assertions.assertThat(persistedAccount.marketingTracking.utmMedium).isEqualTo("utm-medium")
        Assertions.assertThat(persistedAccount.marketingTracking.utmCampaign).isEqualTo("utm-campaign")
    }

    @Test
    fun `throw an exception when the captcha verification fails`() {
        whenever(captchaProvider.validateCaptchaToken(any())).thenReturn(false)

        assertThrows<CaptchaScoreBelowThresholdException> {
            createTeacherAccount(CreateUserRequestFactory.sample())
        }
    }

    @Test
    fun `update contact on hubspot after user creation`() {
        createTeacherAccount(CreateUserRequestFactory.sample())

        verify(marketingService, times(1)).updateProfile(any())
    }

    @Test
    fun `update subscription on hubspot after user creation`() {
        createTeacherAccount(CreateUserRequestFactory.sample())

        verify(marketingService, times(1)).updateSubscription(any())
    }
}
