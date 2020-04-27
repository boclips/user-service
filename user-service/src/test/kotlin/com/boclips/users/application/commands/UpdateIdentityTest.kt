package com.boclips.users.application.commands

import com.boclips.security.testing.setSecurityContext
import com.boclips.users.application.exceptions.InvalidDateException
import com.boclips.users.application.exceptions.OrganisationNotFoundException
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.school.State
import com.boclips.users.api.request.UpdateOrganisationRequest
import com.boclips.users.domain.model.organisation.Address
import com.boclips.users.domain.model.organisation.ExternalOrganisationId
import com.boclips.users.domain.service.UniqueId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.OrganisationFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class UpdateIdentityTest : AbstractSpringIntegrationTest() {
    lateinit var updateOrganisation: UpdateOrganisation

    @BeforeEach
    fun setup() {
        setSecurityContext("user-that-can-update-orgs", UserRoles.UPDATE_ORGANISATIONS)
        updateOrganisation = UpdateOrganisation(
            organisationRepository = organisationRepository
        )
    }

    @Test
    fun `Updates an organisation with a valid request`() {
        val oldExpiryTime = ZonedDateTime.parse("2019-06-06T00:00:00Z")

        val district = organisationRepository.save(
            OrganisationFactory.district(
                name = "my district",
                externalId = ExternalOrganisationId("123"),
                address = Address(
                    state = State(id = "FL", name = "Florida")
                ),
                deal = OrganisationFactory.deal(
                    accessExpiresOn = oldExpiryTime
                )
            )
        )

        val updatedExpiryTime = oldExpiryTime.plusDays(10)
        val updatedExpiryTimeToString = updatedExpiryTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME)
        val request =
            UpdateOrganisationRequest(accessExpiresOn = updatedExpiryTimeToString)
        val updatedOrganisation = updateOrganisation(district.id.value, request)

        assertThat(updatedOrganisation.deal.accessExpiresOn).isEqualTo(updatedExpiryTime)
    }

    @Test
    fun `Throws an error when organisation to update cannot be found`() {
        val nonExistingOrgId = UniqueId()
        assertThrows<OrganisationNotFoundException> {
            updateOrganisation(
                nonExistingOrgId,
                UpdateOrganisationRequest(accessExpiresOn = "2019-06-06T00:00:00Z")
            )
        }
    }

    @Test
    fun `Throws an exception when the request contains an invalid date`() {
        assertThrows<InvalidDateException> {
            updateOrganisation(
                id = "organisation-id",
                request = UpdateOrganisationRequest(accessExpiresOn = "invalid-date")
            )
        }
    }
}
