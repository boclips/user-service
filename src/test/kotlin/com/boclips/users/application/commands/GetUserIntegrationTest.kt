package com.boclips.users.application.commands

import com.boclips.security.testing.setSecurityContext
import com.boclips.users.application.exceptions.NotAuthenticatedException
import com.boclips.users.application.exceptions.PermissionDeniedException
import com.boclips.users.application.exceptions.UserNotFoundException
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.AccountFactory
import com.boclips.users.testsupport.factories.ProfileFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

class GetUserIntegrationTest : AbstractSpringIntegrationTest() {

    @Autowired
    lateinit var getUser: GetUser

    @Test
    fun `get authenticated user`() {
        val userId = UUID.randomUUID().toString()
        setSecurityContext(userId)

        val organisationId = ObjectId().toHexString()
        saveUser(
            UserFactory.sample(
                account = AccountFactory.sample(
                    username = "jane@doe.com",
                    id = userId
                ),
                analyticsId = AnalyticsId(value = "123"),
                profile = ProfileFactory.sample(
                    firstName = "Jane",
                    lastName = "Doe"
                ),
                organisationId = OrganisationId(organisationId)
            )
        )

        val user = getUser(userId)

        assertThat(user.id.value).isEqualTo(userId)
        assertThat(user.profile!!.firstName).isEqualTo("Jane")
        assertThat(user.profile!!.lastName).isEqualTo("Doe")
        assertThat(user.analyticsId!!.value).isEqualTo("123")
        assertThat(user.account.email).isEqualTo("jane@doe.com")
        assertThat(user.organisationId!!.value).isEqualTo(organisationId)
    }

    @Test
    fun `get any user`() {
        val existingUser = saveUser(
            UserFactory.sample(
                account = AccountFactory.sample(
                    username = "jane@doe.com"
                ),
                analyticsId = AnalyticsId(value = "123"),
                profile = ProfileFactory.sample(
                    firstName = "Jane",
                    lastName = "Doe"
                )
            )
        )

        setSecurityContext("user-that-can-view-users", UserRoles.VIEW_USERS)
        val user = getUser(existingUser.id.value)

        assertThat(user.id).isEqualTo(existingUser.id)
    }

    @Test
    fun `cannot obtain user information of another user`() {
        val userId = UUID.randomUUID().toString()
        setSecurityContext("different-user")

        saveUser(UserFactory.sample())

        assertThrows<PermissionDeniedException> { getUser(userId) }
    }

    @Test
    fun `get user throws when security context not populated`() {
        assertThrows<NotAuthenticatedException> {
            getUser("abc")
        }
    }

    @Test
    fun `get user throws when user doesn't exist anywhere`() {
        val userId = UUID.randomUUID().toString()
        setSecurityContext(userId)

        assertThrows<UserNotFoundException> { getUser(userId) }
    }

    @Nested
    inner class WhenUserNotSynchronized {
        @Test
        fun `imports user from account provider when user not found in repository`() {
            val userId = UUID.randomUUID().toString()
            setSecurityContext(userId)

            saveAccount(UserFactory.sample(id = userId))

            val user = getUser(userId)

            assertThat(user.id.value).isEqualTo(userId)
        }
    }
}
