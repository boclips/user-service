package com.boclips.users.domain.service.organisation.resolvers

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.IdentityFactory
import com.boclips.users.testsupport.factories.OrganisationFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class EmailDomainOrganisationResolverTest : AbstractSpringIntegrationTest() {

    lateinit var resolver: OrganisationResolver

    val identity = IdentityFactory.sample(
        username = "me@example.com"
    )

    @BeforeEach
    fun setUp() {
        resolver = EmailDomainOrganisationResolver(organisationRepository)
    }

    @Test
    fun `returns null when nothing matches`() {
        assertThat(resolver.resolve(identity)).isNull()
    }

    @Test
    fun `returns the organisation when only one matches`() {
        val organisation = organisationRepository.save(OrganisationFactory.district(
            domain = "example.com"
        ))

        assertThat(resolver.resolve(identity)).isEqualTo(organisation)
    }

    @Test
    fun `returns null when more than one organisation matches`() {
        organisationRepository.save(OrganisationFactory.district(
            domain = "example.com"
        ))
        organisationRepository.save(OrganisationFactory.district(
            domain = "example.com"
        ))

        assertThat(resolver.resolve(identity)).isNull()
    }

    @Test
    fun `returns null when identity does not contain an email`() {
        assertThat(resolver.resolve(IdentityFactory.sample(username = "abc"))).isNull()
    }
}
