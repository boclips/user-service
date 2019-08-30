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
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
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
                ages = listOf(4, 5, 6),
                country = "United States of America",
                state = "California",
                school = "Sunnydale High School"
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
        assertThat(profile.country).isEqualTo("United States of America")
        assertThat(profile.state).isEqualTo("California")
        assertThat(profile.school).isEqualTo("Sunnydale High School")
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
