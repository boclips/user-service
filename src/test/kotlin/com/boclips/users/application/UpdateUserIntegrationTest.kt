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
        assertThat(user.firstName).isEqualTo("josh")
        assertThat(user.lastName).isEqualTo("fleck")
        assertThat(user.hasOptedIntoMarketing).isTrue()
        assertThat(user.ages).containsExactly(4, 5, 6)
        assertThat(user.subjects).hasSize(1)
        assertThat(user.subjects.first().id).isEqualTo(SubjectId("1"))
    }

    @Test
    fun `update user is idempotent`() {
        val userId = UUID.randomUUID().toString()
        setSecurityContext(userId)

        saveUser(UserFactory.sample(id = userId))

        assertThat(updateUser(userId)).isEqualTo(updateUser(userId))
    }

    @Nested
    @DisplayName("When account already activated")
    inner class AccountActivated {

        @Test
        fun `does not publish a UserActivated event`() {
            val userId = UUID.randomUUID().toString()
            setSecurityContext(userId)
            saveUser(UserFactory.sample(id = userId, activated = true))

            updateUser(userId)

            assertThat(eventBus.hasReceivedEventOfType(UserActivated::class.java)).isFalse()
        }

        @Test
        fun `ignores referral`() {
            val userId = UUID.randomUUID().toString()
            setSecurityContext(userId)

            saveUser(
                UserFactory.sample(
                    activated = true,
                    id = userId,
                    referralCode = "it-is-a-referral",
                    firstName = "Jane",
                    lastName = "Doe",
                    email = "jane@doe.com"
                )
            )

            updateUser(userId)

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
            saveUser(UserFactory.sample(id = userId1, associatedTo = UserSourceFactory.boclipsSample()))

            updateUser(userId1)

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
                    id = userId1,
                    associatedTo = UserSourceFactory.apiClientSample(organisationId = "org-123")
                )
            )

            updateUser(userId1)

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
                    id = userId,
                    referralCode = "it-is-a-referral",
                    firstName = "Jane",
                    lastName = "Doe",
                    email = "jane@doe.com"
                )
            )

            updateUser(userId)

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
            updateUser("peanuts")
        }
    }

    @Test
    fun `cannot obtain user information of another user`() {
        val userId = UUID.randomUUID().toString()
        setSecurityContext("different-user")

        saveUser(UserFactory.sample())

        assertThrows<PermissionDeniedException> { updateUser(userId) }
    }

    @Test
    fun `get user throws when user doesn't exist anywhere`() {
        val userId = UUID.randomUUID().toString()
        setSecurityContext(userId)

        assertThrows<UserNotFoundException> { updateUser(userId) }
    }
}
