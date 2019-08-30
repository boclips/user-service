package com.boclips.users.domain.service

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class OrganisationServiceIntegrationTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var organisationService: OrganisationService

    @Test
    fun `find an organisation by an external id`() {
        val organisation =
            organisationRepository.save(organisationName = "my-school-district", externalId = "external-id")
        val retrievedOrganisation = organisationService.findByExternalId(externalId = "external-id")

        assertThat(organisation).isEqualTo(retrievedOrganisation)
    }
}