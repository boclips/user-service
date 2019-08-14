package com.boclips.users.domain.service

import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.testsupport.factories.UserIdentityFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ConvertIdentityToUserKtTest {
    @Test
    fun `converts identity to user`() {
        val identity = UserIdentityFactory.sample()

        val organisationId = OrganisationId(value = "test")
        val user = convertIdentityToUser(identity = identity, organisationId = organisationId)

        assertThat(user.id).isEqualTo(identity.id)
        assertThat(user.firstName).isEqualTo(identity.firstName)
        assertThat(user.lastName).isEqualTo(identity.lastName)
        assertThat(user.email).isEqualTo(identity.email)
        assertThat(user.associatedTo).isEqualTo(organisationId)
    }
}
