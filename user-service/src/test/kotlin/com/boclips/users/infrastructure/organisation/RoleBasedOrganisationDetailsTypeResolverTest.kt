package com.boclips.users.infrastructure.organisation

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.OrganisationFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RoleBasedOrganisationDetailsTypeResolverTest : AbstractSpringIntegrationTest() {
    @Test
    fun `matches a teacher user to "Boclips for Teachers"`() {
        val organisationId = organisationResolver.resolve(listOf("ROLE_TEACHER"))

        assertThat(organisationId).isNull()
    }

    @Test
    fun `matches a viewsonic user to "ViewSonic MyViewBoard"`() {
        val organisation = saveOrganisation(OrganisationFactory.apiIntegration(role = "ROLE_VIEWSONIC"))

        val resolvedOrganisation = organisationResolver.resolve(listOf("ROLE_VIEWSONIC"))

        assertThat(resolvedOrganisation).isEqualTo(organisation)
    }

    @Test
    fun `matches a pearson user to "Pearson MyRealize"`() {
        val organisation = saveOrganisation(OrganisationFactory.apiIntegration(role = "ROLE_PEARSON_MYREALIZE"))

        val resolvedOrganisation = organisationResolver.resolve(listOf("ROLE_PEARSON_MYREALIZE"))

        assertThat(resolvedOrganisation).isEqualTo(organisation)
    }

    @Test
    fun `does not match if there are only unknown roles`() {
        val matched = organisationResolver.resolve(listOf("ROLE_STUDENT", "uma_offline", "Matt"))

        assertThat(matched).isNull()
    }

    @Test
    fun `can deal with no roles entities`() {
        val matched = organisationResolver.resolve(emptyList())

        assertThat(matched).isNull()
    }
}
