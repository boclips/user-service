package com.boclips.users.application

import com.boclips.security.testing.setSecurityContext
import com.boclips.users.application.exceptions.PermissionDeniedException
import com.boclips.users.application.exceptions.NotAuthenticatedException
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.AccountFactory
import com.boclips.users.testsupport.UserFactory
import com.boclips.users.testsupport.UserIdentityFactory
import org.assertj.core.api.Assertions.assertThat
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

        saveUser(
            UserFactory.sample(
                userId = UserId(value = userId),
                identity = UserIdentityFactory.sample(
                    id = userId,
                    firstName = "Jane",
                    lastName = "Doe",
                    email = "jane@doe.com",
                    isVerified = true
                ),
                account = AccountFactory.sample(
                    id = userId,
                    analyticsId = AnalyticsId(value = "123")
                )
            )
        )

        val resource = getUser(userId)

        assertThat(resource.id).isEqualTo(userId)
        assertThat(resource.firstName).isEqualTo("Jane")
        assertThat(resource.lastName).isEqualTo("Doe")
        assertThat(resource.analyticsId).isEqualTo("123")
        assertThat(resource.email).isEqualTo("jane@doe.com")
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
}