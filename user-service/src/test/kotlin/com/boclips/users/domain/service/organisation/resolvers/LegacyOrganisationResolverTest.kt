package com.boclips.users.domain.service.organisation.resolvers

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.IdentityFactory
import com.boclips.users.testsupport.factories.OrganisationFactory
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class LegacyOrganisationResolverTest : AbstractSpringIntegrationTest() {

    @Test
    fun `returns the organisation by legacy ID when exists`() {
        val legacyId = "ifOfOrgFromLegacyApplication"
        val organisation = OrganisationFactory.district(legacyId = legacyId)
        saveOrganisation(organisation)

        val resolver =
            LegacyOrganisationResolver(
                organisationRepository
            )

        val identity = IdentityFactory.sample(
            legacyOrganisationId = legacyId
        )

        Assertions.assertThat(resolver.resolve(identity)).isEqualTo(organisation)
    }
}