package com.boclips.users.domain.service

import com.boclips.users.testsupport.factories.IdentityFactory
import com.boclips.users.testsupport.factories.UserSourceFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ConvertIdentityToUserKtTest {
    @Test
    fun `converts identity to user`() {
        val identity = IdentityFactory.sample()

        val userSource = UserSourceFactory.apiClientSample(organisationId = "test")
        val user = convertIdentityToUser(
            identity = identity,
            userSource = userSource
        )

        assertThat(user.id).isEqualTo(identity.id)
        assertThat(user.firstName).isEqualTo(identity.firstName)
        assertThat(user.lastName).isEqualTo(identity.lastName)
        assertThat(user.email).isEqualTo(identity.email)
        assertThat(user.associatedTo).isEqualTo(userSource)
    }
}
