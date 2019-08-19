package com.boclips.users.application

import com.boclips.security.testing.setSecurityContext
import com.boclips.users.application.exceptions.NotAuthenticatedException
import com.boclips.users.application.exceptions.PermissionDeniedException
import com.boclips.users.domain.model.AccountNotFoundException
import com.boclips.users.domain.model.UserSource
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.AccountFactory
import com.boclips.users.testsupport.factories.ProfileFactory
import com.boclips.users.testsupport.factories.UserFactory
import com.boclips.users.testsupport.factories.UserSourceFactory
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
    fun `get user`() {
        val userId = UUID.randomUUID().toString()
        setSecurityContext(userId)

        val organisationId = ObjectId().toHexString()
        saveUser(
            UserFactory.sample(
                account = AccountFactory.sample(
                    username = "jane@doe.com",
                    id = userId,
                    associatedTo = UserSourceFactory.apiClientSample(organisationId = organisationId)
                ),
                analyticsId = AnalyticsId(value = "123"),
                profile = ProfileFactory.sample(
                    firstName = "Jane",
                    lastName = "Doe"
                )
            )
        )

        val resource = getUser(userId)

        assertThat(resource.id).isEqualTo(userId)
        assertThat(resource.firstName).isEqualTo("Jane")
        assertThat(resource.lastName).isEqualTo("Doe")
        assertThat(resource.analyticsId).isEqualTo("123")
        assertThat(resource.email).isEqualTo("jane@doe.com")
        assertThat(resource.organisationId).isEqualTo(organisationId)
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

        assertThrows<AccountNotFoundException> { getUser(userId) }
    }

    @Nested
    inner class WhenUserNotSynchronized {
        @Test
        fun `imports user from account provider when user not found in repository`() {
            val userId = UUID.randomUUID().toString()
            setSecurityContext(userId)

            saveAccount(UserFactory.sample(id = userId))

            val resource = getUser(userId)

            assertThat(resource.id).isEqualTo(userId)
        }

        @Test
        fun `populates the organisation when synchronizing user`() {
            val userId = UUID.randomUUID().toString()

            setSecurityContext(userId, "TEACHER")
            val organisation = saveOrganisation("Boclips for Teachers")

            saveAccount(
                UserFactory.sample(
                    account = AccountFactory.sample(
                        id = userId,
                        associatedTo = UserSource.ApiClient(organisation.id)
                    )
                )
            )

            val resource = getUser(userId)

            assertThat(resource.organisationId).isEqualTo(organisation.id.value)
        }
    }
}
