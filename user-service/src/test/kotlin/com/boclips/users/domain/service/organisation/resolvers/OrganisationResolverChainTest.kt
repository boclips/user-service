package com.boclips.users.domain.service.organisation.resolvers

import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.user.Identity
import com.boclips.users.testsupport.factories.IdentityFactory
import com.boclips.users.testsupport.factories.OrganisationFactory.Companion.district
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class OrganisationResolverChainTest {

    @Test
    fun `delegates to internal resolvers in order`() {
        val resolver =
            OrganisationResolverChain(
                FakeResolver(null),
                FakeResolver(district(name = "A")),
                FakeResolver(district(name = "B"))
            )

        val organisation = resolver.resolve(IdentityFactory.sample())

        assertThat(organisation).isNotNull
        assertThat(organisation?.name).isEqualTo("A")
    }
}

class FakeResolver(val organisation: Organisation?) :
    OrganisationResolver {
    override fun resolve(identity: Identity): Organisation? {
        return organisation
    }
}
