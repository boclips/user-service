package com.boclips.users.application

import com.boclips.security.testing.setSecurityContext
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.account.AccountId
import com.boclips.users.domain.service.ReferralProvider
import com.boclips.users.presentation.exceptions.SecurityContextUserNotFoundException
import com.boclips.users.presentation.requests.CreateUserRequest
import com.boclips.users.presentation.requests.UserActivationRequest
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.CreateUserRequestFactory
import com.boclips.users.testsupport.UserFactory
import com.boclips.users.testsupport.UserIdentityFactory
import com.nhaarman.mockitokotlin2.check
import com.nhaarman.mockitokotlin2.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

class UserActionsIntegrationTest : AbstractSpringIntegrationTest() {

    @Autowired
    lateinit var userActions: UserActions

    @Autowired
    lateinit var referralProvider: ReferralProvider

    @Test
    fun `create account`() {
        val createdAccount = userActions.create(
            CreateUserRequest(
                firstName = "Hans",
                lastName = "Muster",
                email = "hans@muster.com",
                password = "hansli",
                analyticsId = "mxp123",
                subjects = "some stuff",
                referralCode = null
            )
        )

        assertThat(accountRepository.findById(AccountId(value = createdAccount.userId.value))).isNotNull
        assertThat(identityProvider.getUserById(createdAccount.identity.id)).isNotNull
    }

    @Test
    fun `activates new user if user does not exist`() {
        setSecurityContext("user@example.com")

        val activatedUser = userActions.activate(UserActivationRequest())

        val persistedUser = accountRepository.findById(activatedUser.id)
        assertThat(persistedUser).isNotNull
        assertThat(persistedUser!!.activated).isTrue()
    }

    @Test
    fun `activate user is idempotent`() {
        setSecurityContext("user@example.com")

        assertThat(userActions.activate(UserActivationRequest()))
            .isEqualTo(
                userActions.activate(UserActivationRequest())
            )
    }

    @Test
    fun `activate user does not mark as referred if referral code is null`() {
        setSecurityContext("user@example.com")

        val activatedUser = userActions.activate(UserActivationRequest())

        val persistedUser = accountRepository.findById(activatedUser.id)
        assertThat(persistedUser!!.isReferral).isFalse()
    }

    @Test
    fun `activate user tracks whether it's a referral or not`() {
        val identity = UUID.randomUUID().toString()

        setSecurityContext(identity)

        saveUser(
            UserFactory.sample(
                userId = UserId(value = identity),
                identity = UserIdentityFactory.sample(
                    id = identity,
                    firstName = "Jane",
                    lastName = "Doe",
                    email = "jane@doe.com",
                    isVerified = true
                )
            )
        )

        val activatedUser = userActions.activate(
            UserActivationRequest(
                referralCode = "it-is-a-referral"
            )
        )

        val persistedAccount = accountRepository.findById(activatedUser.id)
        assertThat(persistedAccount!!.isReferral).isTrue()

        verify(referralProvider).createReferral(check {
            assertThat(it.referralCode).isEqualTo("it-is-a-referral")
            assertThat(it.firstName).isEqualTo("Jane")
            assertThat(it.lastName).isEqualTo("Doe")
            assertThat(it.email).isEqualTo("jane@doe.com")
            assertThat(it.status).isEqualTo("qualified")
            assertThat(it.externalIdentifier).isEqualTo(identity)
        })
    }

    @Test
    fun `activateUser when security context not populated throws`() {
        assertThrows<SecurityContextUserNotFoundException> {
            userActions.activate(UserActivationRequest())
        }
    }

    @Test
    fun `create user stores user information`() {
        val user = userActions.create(
            CreateUserRequestFactory.sample(
                subjects = "maths",
                referralCode = "referral-code-123",
                analyticsId = "123"
            )
        )

        val persistedAccount = accountRepository.findById(user.account.id)

        assertThat(persistedAccount!!.isReferral).isTrue()
        assertThat(persistedAccount.referralCode).isEqualTo("referral-code-123")

        assertThat(persistedAccount.subjects).isEqualTo("maths")
        assertThat(persistedAccount.analyticsId!!.value).isEqualTo("123")
    }
}
