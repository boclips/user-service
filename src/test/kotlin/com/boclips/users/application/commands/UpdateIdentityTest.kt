package com.boclips.users.application.commands

import com.boclips.security.testing.setSecurityContext
import com.boclips.users.application.exceptions.OrganisationNotFoundException
import com.boclips.users.application.exceptions.InvalidDateException
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.school.State
import com.boclips.users.presentation.requests.UpdateOrganisationRequest
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.OrganisationDetailsFactory
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
            OrganisationFactory.sample(
                details = OrganisationDetailsFactory.district(
                    name = "my district",
                    externalId = "123",
                    state = State(id = "FL", name = "Florida")
                ),
                accessExpiresOn = oldExpiryTime
            )
        )

        val updatedExpiryTime = oldExpiryTime.plusDays(10)
        val updatedExpiryTimeToString = updatedExpiryTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME)
        val request = UpdateOrganisationRequest(accessExpiresOn = updatedExpiryTimeToString)
        val updatedOrganisation = updateOrganisation(district.id.value, request)

        assertThat(updatedOrganisation.accessExpiresOn).isEqualTo(updatedExpiryTime)
    }

    @Test
    fun `Throws an error when organisation to update cannot be found`() {

        assertThrows<OrganisationNotFoundException> {
            updateOrganisation("non-existent-org", UpdateOrganisationRequest(accessExpiresOn = "2019-06-06T00:00:00Z"))
        }
    }

    @Test
    fun `Throws an exception when the request contains an invalid date`() {
        assertThrows<InvalidDateException> {
            updateOrganisation(
                id = "organisation-id",
                request = UpdateOrganisationRequest("invalid-date")
            )
        }
    }
}
