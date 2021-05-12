package com.boclips.users.application.commands

import com.boclips.security.testing.setSecurityContext
import com.boclips.users.application.commands.GetTrackableUserId.Companion.ANONYMOUS_USER_ID
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.IdentityFactory
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class GetTrackableUserIdIntegrationTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var getTrackableUserId: GetTrackableUserId

    @Test
    fun `return user id if trackable`() {
        saveUser(UserFactory.sample(id = "testUser"))
        setSecurityContext("testUser")

        val userId = getTrackableUserId.invoke()
        assertThat(userId).isEqualTo("testUser")
    }

    @Test
    fun `return anonymous id if user is not trackable`() {
        saveUser(
            UserFactory.sample(
                identity = IdentityFactory.sample(id = "testUser"),
                profile = null,
                organisation = OrganisationFactory.school()
            )
        )
        setSecurityContext("testUser")

        val userId = getTrackableUserId.invoke()
        assertThat(userId).isEqualTo(ANONYMOUS_USER_ID)
    }

    @Test
    fun `does not fetch user when user us anonymous`() {
        setSecurityContext("anonymousUser")

        val userId = getTrackableUserId.invoke()
        assertThat(userId).isEqualTo(ANONYMOUS_USER_ID)
    }
}
