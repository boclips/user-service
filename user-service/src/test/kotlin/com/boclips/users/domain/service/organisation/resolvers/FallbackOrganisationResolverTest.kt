package com.boclips.users.domain.service.organisation.resolvers

import com.boclips.users.domain.model.organisation.OrganisationTag.DEFAULT_ORGANISATION
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.IdentityFactory
import com.boclips.users.testsupport.factories.OrganisationFactory.Companion.district
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FallbackOrganisationResolverTest : AbstractSpringIntegrationTest() {

    @Test
    fun `returns the only default organisation when exists`() {
        saveOrganisation(district(tags = setOf(DEFAULT_ORGANISATION)))

        val resolver =
            FallbackOrganisationResolver(
                organisationRepository
            )

        assertThat(resolver.resolve(IdentityFactory.sample())).isNotNull
    }

    @Test
    fun `returns nothing when no default orgs`() {
        val resolver =
            FallbackOrganisationResolver(
                organisationRepository
            )

        assertThat(resolver.resolve(IdentityFactory.sample())).isNull()
    }

    @Test
    fun `returns nothing when multiple default orgs`() {
        saveOrganisation(district(tags = setOf(DEFAULT_ORGANISATION)))
        saveOrganisation(district(tags = setOf(DEFAULT_ORGANISATION)))

        val resolver =
            FallbackOrganisationResolver(
                organisationRepository
            )

        assertThat(resolver.resolve(IdentityFactory.sample())).isNull()
    }
}
