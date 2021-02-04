package com.boclips.users.domain.service.organisation.resolvers

import com.boclips.users.domain.model.organisation.OrganisationTag
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.IdentityFactory
import com.boclips.users.testsupport.factories.OrganisationFactory.Companion.district
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class OrganisationResolverIntegrationTest : AbstractSpringIntegrationTest() {

    @BeforeEach
    fun setUp() {
        saveOrganisation(
            district(
                name = "Email",
                role = null,
                domain = "example.com",
                tags = emptySet(),
                legacyId = null
            )
        )
        saveOrganisation(
            district(
                name = "Role",
                role = "ROLE_X",
                domain = null,
                tags = emptySet(),
                legacyId = null
            )
        )
        saveOrganisation(
            district(
                name = "Default",
                role = null,
                domain = null,
                tags = setOf(OrganisationTag.DEFAULT_ORGANISATION),
                legacyId = null
            )
        )
        saveOrganisation(
            district(
                name = "OrganisationWithLegacyCounterpart",
                role = null,
                domain = null,
                tags = emptySet(),
                legacyId = "idOfOrgFromLegacyApplication"
            )
        )
    }

    @Test
    fun `organisations matching by email domain have priority over those matching by role`() {
        val identity = IdentityFactory.sample(
            username = "bob@example.com",
            roles = listOf("ROLE_X")
        )

        assertThat(organisationResolver.resolve(identity)).isNotNull
        assertThat(organisationResolver.resolve(identity)?.name).isEqualTo("Email")
    }

    @Test
    fun `role match returned when no organisation matches by email domain`() {
        val identity = IdentityFactory.sample(
            username = "abc",
            roles = listOf("ROLE_X")
        )

        assertThat(organisationResolver.resolve(identity)).isNotNull
        assertThat(organisationResolver.resolve(identity)?.name).isEqualTo("Role")
    }

    @Test
    fun `default organisation returned when nothing else matches`() {
        val identity = IdentityFactory.sample(
            username = "abc",
            roles = emptyList()
        )

        assertThat(organisationResolver.resolve(identity)).isNotNull
        assertThat(organisationResolver.resolve(identity)?.name).isEqualTo("Default")
    }

    @Test
    fun `organisation with legacy counterpart is returned when legacy ID is defined`() {
        val identity = IdentityFactory.sample(
            username = "abc",
            roles = emptyList(),
            legacyOrganisationId = "idOfOrgFromLegacyApplication"
        )

        assertThat(organisationResolver.resolve(identity)).isNotNull
        assertThat(organisationResolver.resolve(identity)?.name).isEqualTo("OrganisationWithLegacyCounterpart")
    }
}
