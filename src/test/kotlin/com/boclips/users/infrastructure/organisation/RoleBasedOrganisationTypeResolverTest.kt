package com.boclips.users.infrastructure.organisation

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RoleBasedOrganisationTypeResolverTest : AbstractSpringIntegrationTest() {
    @Test
    fun `matches a teacher user to "Boclips for Teachers"`() {
        val organisationId = organisationIdResolver.resolve(listOf("ROLE_TEACHER"))

        assertThat(organisationId).isNull()
    }

    @Test
    fun `matches a viewsonic user to "ViewSonic MyViewBoard"`() {
        val organisation = saveOrganisation(role = "ROLE_VIEWSONIC")

        val organisationId = organisationIdResolver.resolve(listOf("ROLE_VIEWSONIC"))

        assertThat(organisationId).isEqualTo(organisation.id)
    }

    @Test
    fun `matches a pearson user to "Pearson MyRealize"`() {
        val organisation = saveOrganisation(role = "ROLE_PEARSON_MYREALIZE")

        val organisationId = organisationIdResolver.resolve(listOf("ROLE_PEARSON_MYREALIZE"))

        assertThat(organisationId).isEqualTo(organisation.id)
    }

    @Test
    fun `does not match if there are only unknown roles`() {
        val matched = organisationIdResolver.resolve(listOf("ROLE_STUDENT", "uma_offline", "Matt"))

        assertThat(matched).isNull()
    }

    @Test
    fun `can deal with no roles entities`() {
        val matched = organisationIdResolver.resolve(emptyList())

        assertThat(matched).isNull()
    }
}
