package com.boclips.users.application.commands

import com.boclips.users.application.exceptions.InvalidDateException
import com.boclips.users.domain.model.school.State
import com.boclips.users.presentation.requests.UpdateOrganisationRequest
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.google.api.client.util.DateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class UpdateOrganisationTest : AbstractSpringIntegrationTest() {
    lateinit var updateOrganisation: UpdateOrganisation
    @BeforeEach
    fun setup() {
        updateOrganisation = UpdateOrganisation(
            organisationAccountRepository = organisationAccountRepository
        )
    }

    @Test
    fun `Updates an organisation with a valid request`() {
        val oldExpiryTime = ZonedDateTime.now()

        val district = organisationAccountRepository.save(
            district = OrganisationFactory.district(
                name = "my district",
                externalId = "123",
                state = State(id = "FL", name = "Florida")
            ),
            accessExpiresOn = oldExpiryTime
        )
        val updatedExpiryTime = ZonedDateTime.now().plusWeeks(1)
        val updatedExpiryTimeToString = updatedExpiryTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME)
        val request = UpdateOrganisationRequest(accessExpiresOn = updatedExpiryTimeToString)
        val updatedOrganisation = updateOrganisation(district.id.value, request)

        assertThat(updatedOrganisation?.accessExpiresOn).isEqualTo(updatedExpiryTime)
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
