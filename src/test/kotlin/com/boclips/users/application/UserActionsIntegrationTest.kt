package com.boclips.users.application

import com.boclips.security.testing.setSecurityContext
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.account.AccountId
import com.boclips.users.presentation.exceptions.SecurityContextUserNotFoundException
import com.boclips.users.presentation.requests.CreateUserRequest
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.AccountFactory
import com.boclips.users.testsupport.CreateUserRequestFactory
import com.boclips.users.testsupport.UserFactory
import com.boclips.users.testsupport.UserIdentityFactory
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.check
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

class UserActionsIntegrationTest : AbstractSpringIntegrationTest() {

    @Autowired
    lateinit var userActions: UserActions

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
    fun `create account stores user information`() {
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

    @Test
    fun `create an account and update contact on hubspot`() {
        userActions.create(
            CreateUserRequestFactory.sample(
                subjects = "maths",
                referralCode = "referral-code-123",
                analyticsId = "123"
            )
        )

        verify(customerManagementProvider, times(1)).update(any())
    }

    @Test
    fun `activates new user if user does not exist`() {
        setSecurityContext("user@example.com")

        val activatedUser = userActions.activate()

        val persistedUser = accountRepository.findById(activatedUser.id)
        assertThat(persistedUser).isNotNull
        assertThat(persistedUser!!.activated).isTrue()
    }

    @Test
    fun `activate user is idempotent`() {
        setSecurityContext("user@example.com")

        assertThat(userActions.activate())
            .isEqualTo(
                userActions.activate()
            )
    }

    @Test
    fun `activate user does not delegate to referral provider by default`() {
        setSecurityContext("user@example.com")

        userActions.activate()

        verify(referralProvider, times(0)).createReferral(any())
    }

    @Test
    fun `activate user delegates to referral provider`() {
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
                ),
                account = AccountFactory.sample(
                    id = identity,
                    isReferral = true,
                    referralCode = "it-is-a-referral"
                )
            )
        )

        userActions.activate()

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
            userActions.activate()
        }
    }
}
