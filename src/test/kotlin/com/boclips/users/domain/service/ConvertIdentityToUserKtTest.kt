package com.boclips.users.domain.service

import com.boclips.users.testsupport.factories.IdentityFactory
import com.boclips.users.testsupport.factories.UserSourceFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ConvertIdentityToUserKtTest {
    @Test
    fun `converts identity to user`() {
        val userSource = UserSourceFactory.apiClientSample(organisationId = "test")
        val identity = IdentityFactory.sample(userSource = userSource)

        val user = convertIdentityToUser(identity = identity)

        assertThat(user.id).isEqualTo(identity.id)
        assertThat(user.email).isEqualTo(identity.email)
        assertThat(user.associatedTo).isEqualTo(userSource)
    }
}
