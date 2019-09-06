package com.boclips.users.domain.service

import com.boclips.users.infrastructure.organisation.OrganisationType
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.OrganisationFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class OrganisationServiceIntegrationTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var organisationService: OrganisationService

    @Test
    fun `find an organisation by an external id`() {
        val organisation =
            organisationAccountRepository.save(organisation = OrganisationFactory.district(name = "my-school-district", externalId = "external-id"))
        val retrievedOrganisation = organisationService.findByExternalId(externalId = "external-id")

        assertThat(organisation).isEqualTo(retrievedOrganisation)
    }
}