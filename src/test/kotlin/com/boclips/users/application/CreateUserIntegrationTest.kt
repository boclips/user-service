package com.boclips.users.application

import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.presentation.requests.CreateUserRequest
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.CreateUserRequestFactory
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class CreateUserIntegrationTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var createUser: CreateUser

    @Test
    fun `create account without optional values`() {
        val createdAccount = createUser(
            CreateUserRequest(
                firstName = "Hans",
                lastName = "Muster",
                email = "hans@muster.com",
                password = "hansli"
            )
        )

        val account = accountRepository.findById(createdAccount.account.id)
        Assertions.assertThat(account).isNotNull
        Assertions.assertThat(account!!.isReferral()).isFalse()
        Assertions.assertThat(account.referralCode).isEmpty()
        Assertions.assertThat(account.subjects).isEmpty()
        Assertions.assertThat(account.analyticsId).isEqualTo(AnalyticsId(value = ""))

        val identity = identityProvider.getUserById(createdAccount.account.id)
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

        val persistedAccount = accountRepository.findById(user.account.id)!!

        Assertions.assertThat(persistedAccount.isReferral()).isTrue()
        Assertions.assertThat(persistedAccount.referralCode).isEqualTo("referral-code-123")
        Assertions.assertThat(persistedAccount.subjects).isEqualTo("maths")
        Assertions.assertThat(persistedAccount.analyticsId!!.value).isEqualTo("123")
    }

    @Test
    fun `update contact on hubspot after user creation`() {
        createUser(CreateUserRequestFactory.sample())

        verify(customerManagementProvider, times(1)).update(any())
    }
}