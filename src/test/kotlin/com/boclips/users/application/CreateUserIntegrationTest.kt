package com.boclips.users.application

import com.boclips.users.application.exceptions.CaptchaScoreBelowThresholdException
import com.boclips.users.application.exceptions.InvalidSubjectException
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.presentation.requests.CreateUserRequest
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

class CreateUserIntegrationTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var createUser: CreateUser

    @Test
    fun `create account without optional values`() {
        whenever(subjectService.getSubjectsById(any())).thenReturn(
            emptyList()
        )

        val createdAccount = createUser(
            CreateUserRequest(
                email = "hans@muster.com",
                password = "hansli",
                recaptchaToken = "SOMERECAPTCHATOKENHERE"
            )
        )

        val account = userRepository.findById(createdAccount.id)
        Assertions.assertThat(account).isNotNull
        Assertions.assertThat(account!!.isReferral()).isFalse()
        Assertions.assertThat(account.referralCode).isEmpty()
        Assertions.assertThat(account.subjects).isEmpty()
        Assertions.assertThat(account.ages).isEmpty()
        Assertions.assertThat(account.analyticsId).isEqualTo(AnalyticsId(value = ""))

        val identity = identityProvider.getUserById(createdAccount.id)
        Assertions.assertThat(identity).isNotNull
        Assertions.assertThat(identity!!.email).isEqualTo("hans@muster.com")
    }

    @Test
    fun `create account with referral and analytics information`() {
        val user = createUser(
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
        Assertions.assertThat(persistedAccount.subjects).isEmpty()
        Assertions.assertThat(persistedAccount.ages).isEmpty()
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
            createUser(CreateUserRequestFactory.sample())
        }
    }

    @Test
    fun `update contact on hubspot after user creation`() {
        createUser(CreateUserRequestFactory.sample())

        verify(marketingService, times(1)).updateProfile(any())
    }

    @Test
    fun `update subscription on hubspot after user creation`() {
        createUser(CreateUserRequestFactory.sample())

        verify(marketingService, times(1)).updateSubscription(any())
    }

}
