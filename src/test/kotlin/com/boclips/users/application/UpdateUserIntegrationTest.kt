package com.boclips.users.application

import com.boclips.eventbus.events.user.UserActivated
import com.boclips.security.testing.setSecurityContext
import com.boclips.users.application.exceptions.NotAuthenticatedException
import com.boclips.users.application.exceptions.PermissionDeniedException
import com.boclips.users.application.exceptions.UserNotFoundException
import com.boclips.users.domain.model.SubjectId
import com.boclips.users.domain.model.UserId
import com.boclips.users.presentation.requests.UpdateUserRequest
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.AccountFactory
import com.boclips.users.testsupport.factories.ProfileFactory
import com.boclips.users.testsupport.factories.UpdateUserRequestFactory
import com.boclips.users.testsupport.factories.UserFactory
import com.boclips.users.testsupport.factories.UserSourceFactory
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

class UpdateUserIntegrationTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var updateUser: UpdateUser

    @Test
    fun `update user information`() {
        val userId = UUID.randomUUID().toString()
        setSecurityContext(userId)

        saveUser(UserFactory.sample(id = userId))
        updateUser(
            userId, UpdateUserRequest(
                firstName = "josh",
                lastName = "fleck",
                hasOptedIntoMarketing = true,
                subjects = listOf("Maths"),
                ages = listOf(4, 5, 6)
            )
        )

        val user = userRepository.findById(UserId(userId))!!
        val profile = user.profile!!
        assertThat(profile.firstName).isEqualTo("josh")
        assertThat(profile.lastName).isEqualTo("fleck")
        assertThat(profile.hasOptedIntoMarketing).isTrue()
        assertThat(profile.ages).containsExactly(4, 5, 6)
        assertThat(profile.subjects).hasSize(1)
        assertThat(profile.subjects.first().id).isEqualTo(SubjectId("1"))
    }

    @Test
    fun `update user is idempotent`() {
        val userId = UUID.randomUUID().toString()
        setSecurityContext(userId)

        saveUser(UserFactory.sample(id = userId))

        val updateUserRequest = UpdateUserRequestFactory.sample()
        assertThat(updateUser(userId, updateUserRequest)).isEqualTo(updateUser(userId, updateUserRequest))
    }

    @Nested
    @DisplayName("When account already activated")
    inner class AccountActivated {

        @Test
        fun `does not publish a UserActivated event`() {
            val userId = UUID.randomUUID().toString()
            setSecurityContext(userId)
            saveUser(
                UserFactory.sample(
                    account = AccountFactory.sample(id = userId),
                    profile = ProfileFactory.sample()
                )
            )

            updateUser(userId, UpdateUserRequestFactory.sample())

            assertThat(eventBus.hasReceivedEventOfType(UserActivated::class.java)).isFalse()
        }

        @Test
        fun `ignores referral`() {
            val userId = UUID.randomUUID().toString()
            setSecurityContext(userId)

            saveUser(
                UserFactory.sample(
                    account = AccountFactory.sample(id = userId),
                    profile = ProfileFactory.sample(),
                    referralCode = "it-is-a-referral"
                )
            )

            updateUser(userId, UpdateUserRequestFactory.sample())

            verifyZeroInteractions(referralProvider)
        }
    }

    @Nested
    @DisplayName("When account not activated")
    inner class AccountNotActivated {

        @Test
        fun `update user publishes an event for teacher user`() {
            val userId1 = "${UUID.randomUUID()}@boclips.com"
            setSecurityContext(userId1)
            saveUser(
                UserFactory.sample(
                    account = AccountFactory.sample(
                        id = userId1,
                        platform = UserSourceFactory.boclipsSample()
                    ),
                    profile = null
                )
            )

            updateUser(userId1, UpdateUserRequestFactory.sample())

            val event = eventBus.getEventOfType(UserActivated::class.java)
            assertThat(event.totalUsers).isEqualTo(1)
            assertThat(event.activatedUsers).isEqualTo(1)
            assertThat(event.user.isBoclipsEmployee).isFalse()
            assertThat(event.user.organisationId).isEqualTo(null)
        }

        @Test
        fun `update user publishes an event for api user`() {
            val userId1 = "${UUID.randomUUID()}@boclips.com"
            setSecurityContext(userId1)
            saveUser(
                UserFactory.sample(
                    account = AccountFactory.sample(
                        id = userId1,
                        platform = UserSourceFactory.apiClientSample(organisationId = "org-123")
                    ),
                    profile = null
                )
            )

            updateUser(userId1, UpdateUserRequestFactory.sample())

            val event = eventBus.getEventOfType(UserActivated::class.java)
            assertThat(event.totalUsers).isEqualTo(1)
            assertThat(event.activatedUsers).isEqualTo(1)
            assertThat(event.user.isBoclipsEmployee).isFalse()
            assertThat(event.user.organisationId).isEqualTo("org-123")
        }

        @Test
        fun `update user marks referral`() {
            val userId = UUID.randomUUID().toString()
            setSecurityContext(userId)

            saveUser(
                UserFactory.sample(
                    account = AccountFactory.sample(
                        id = userId,
                        username = "jane@doe.com"
                    ),
                    referralCode = "it-is-a-referral",
                    profile = null
                )
            )

            updateUser(userId, UpdateUserRequest(
                firstName = "Jane",
                lastName = "Doe",
                hasOptedIntoMarketing = false
            ))

            verify(referralProvider).createReferral(com.nhaarman.mockitokotlin2.check {
                Assertions.assertThat(it.referralCode).isEqualTo("it-is-a-referral")
                Assertions.assertThat(it.firstName).isEqualTo("Jane")
                Assertions.assertThat(it.lastName).isEqualTo("Doe")
                Assertions.assertThat(it.email).isEqualTo("jane@doe.com")
                Assertions.assertThat(it.status).isEqualTo("qualified")
                Assertions.assertThat(it.externalIdentifier).isEqualTo(userId)
            })
        }
    }

    @Test
    fun `cannot update user when security context not populated`() {
        assertThrows<NotAuthenticatedException> {
            updateUser("peanuts", UpdateUserRequestFactory.sample())
        }
    }

    @Test
    fun `cannot obtain user information of another user`() {
        val userId = UUID.randomUUID().toString()
        setSecurityContext("different-user")

        saveUser(UserFactory.sample())

        assertThrows<PermissionDeniedException> { updateUser(userId, UpdateUserRequestFactory.sample()) }
    }

    @Test
    fun `get user throws when user doesn't exist anywhere`() {
        val userId = UUID.randomUUID().toString()
        setSecurityContext(userId)

        assertThrows<UserNotFoundException> { updateUser(userId, UpdateUserRequestFactory.sample()) }
    }
}
