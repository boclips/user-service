package com.boclips.users.application

import com.boclips.users.application.exceptions.CaptchaScoreBelowThresholdException
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.presentation.requests.CreateUserRequest
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.CreateUserRequestFactory
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
        val createdAccount = createUser(
            CreateUserRequest(
                firstName = "Hans",
                lastName = "Muster",
                email = "hans@muster.com",
                password = "hansli",
                recaptchaToken = "SOMERECAPTCHATOKENHERE",
                hasOptedIntoMarketing = true
            )

        )

        val account = userRepository.findById(createdAccount.id)
        Assertions.assertThat(account).isNotNull
        Assertions.assertThat(account!!.isReferral()).isFalse()
        Assertions.assertThat(account.referralCode).isEmpty()
        Assertions.assertThat(account.subjects).isEmpty()
        Assertions.assertThat(account.analyticsId).isEqualTo(AnalyticsId(value = ""))
        Assertions.assertThat(account.hasOptedIntoMarketing).isTrue()

        val identity = identityProvider.getUserById(createdAccount.id)
        Assertions.assertThat(identity).isNotNull
        Assertions.assertThat(identity!!.firstName).isEqualTo("Hans")
        Assertions.assertThat(identity.lastName).isEqualTo("Muster")
        Assertions.assertThat(identity.email).isEqualTo("hans@muster.com")
    }

    @Test
    fun `create account with referral, subject and analytics information`() {
        val user = createUser(
            CreateUserRequestFactory.sample(
                subjects = "maths",
                referralCode = "referral-code-123",
                analyticsId = "123"
            )
        )

        val persistedAccount = userRepository.findById(user.id)!!

        Assertions.assertThat(persistedAccount.isReferral()).isTrue()
        Assertions.assertThat(persistedAccount.referralCode).isEqualTo("referral-code-123")
        Assertions.assertThat(persistedAccount.subjects).isEqualTo("maths")
        Assertions.assertThat(persistedAccount.analyticsId!!.value).isEqualTo("123")
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

        verify(customerManagementProvider, times(1)).update(any())
    }
}