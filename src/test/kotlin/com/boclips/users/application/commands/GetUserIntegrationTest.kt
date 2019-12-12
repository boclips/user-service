package com.boclips.users.application.commands

import com.boclips.security.testing.setSecurityContext
import com.boclips.users.application.exceptions.NotAuthenticatedException
import com.boclips.users.application.exceptions.PermissionDeniedException
import com.boclips.users.application.exceptions.UserNotFoundException
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.IdentityFactory
import com.boclips.users.testsupport.factories.ProfileFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
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

        val school = saveSchool()
        saveUser(
            UserFactory.sample(
                identity = IdentityFactory.sample(
                    username = "jane@doe.com",
                    id = userId
                ),
                analyticsId = AnalyticsId(value = "123"),
                profile = ProfileFactory.sample(
                    firstName = "Jane",
                    lastName = "Doe"
                ),
                organisationAccountId = school.id
            )
        )

        val userResource = getUser(userId)

        assertThat(userResource.id).isEqualTo(userId)
        assertThat(userResource.firstName).isEqualTo("Jane")
        assertThat(userResource.lastName).isEqualTo("Doe")
        assertThat(userResource.analyticsId!!).isEqualTo("123")
        assertThat(userResource.email).isEqualTo("jane@doe.com")
        assertThat(userResource.organisationAccountId).isEqualTo(school.id.value)
        assertThat(userResource.organisation!!.name).isEqualTo(school.organisation.name)
        assertThat(userResource.organisation!!.state!!.name).isEqualTo(school.organisation.state!!.name)
        assertThat(userResource.organisation!!.state!!.id).isEqualTo(school.organisation.state!!.id)
        assertThat(userResource.organisation!!.country!!.name).isEqualTo(school.organisation.country.name)
        assertThat(userResource.organisation!!.country!!.id).isEqualTo(school.organisation.country.id)
    }

    @Test
    fun `get any user`() {
        val existingUser = saveUser(
            UserFactory.sample(
                identity = IdentityFactory.sample(
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

        assertThat(user.id).isEqualTo(existingUser.id.value)
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

            assertThat(user.id).isEqualTo(userId)
        }
    }
}
