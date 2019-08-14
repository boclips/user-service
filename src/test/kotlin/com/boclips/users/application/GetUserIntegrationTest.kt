package com.boclips.users.application

import com.boclips.security.testing.setSecurityContext
import com.boclips.users.application.exceptions.NotAuthenticatedException
import com.boclips.users.application.exceptions.PermissionDeniedException
import com.boclips.users.domain.model.UserNotFoundException
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
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
    fun `get user`() {
        val userId = UUID.randomUUID().toString()
        setSecurityContext(userId)

        val organisationId = ObjectId().toHexString()
        saveUser(
            UserFactory.sample(
                id = userId,
                analyticsId = AnalyticsId(value = "123"),
                firstName = "Jane",
                lastName = "Doe",
                email = "jane@doe.com",
                associatedTo = OrganisationId(value = organisationId)
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

        assertThrows<UserNotFoundException> { getUser(userId) }
    }

    @Nested
    inner class WhenUserNotSynchronized {
        @Test
        fun `attempts to synchronise user with identity provider`() {
            val userId = UUID.randomUUID().toString()
            setSecurityContext(userId)

            saveIdentity(UserFactory.sample(id = userId))

            val resource = getUser(userId)

            assertThat(resource.id).isEqualTo(userId)
        }

        @Test
        fun `populates the organisation when synchronizing user`() {
            val userId = UUID.randomUUID().toString()

            setSecurityContext(userId, "TEACHER")
            val organisation = saveOrganisation("Boclips for Teachers")

            saveIdentity(UserFactory.sample(id = userId))

            val resource = getUser(userId)

            assertThat(resource.organisationId).isEqualTo(organisation.id.value)
        }
    }
}
