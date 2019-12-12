package com.boclips.users.application.commands

import com.boclips.users.application.exceptions.InvalidDateException
import com.boclips.users.application.exceptions.OrganisationNotFoundException
import com.boclips.users.domain.model.school.State
import com.boclips.users.presentation.requests.UpdateAccountRequest
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.OrganisationFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class UpdateIdentityTest : AbstractSpringIntegrationTest() {
    lateinit var updateAccount: UpdateAccount
    @BeforeEach
    fun setup() {
        updateAccount = UpdateAccount(
            accountRepository = accountRepository
        )
    }

    @Test
    fun `Updates an organisation with a valid request`() {
        val oldExpiryTime = ZonedDateTime.parse("2019-06-06T00:00:00Z")

        val district = accountRepository.save(
            district = OrganisationFactory.district(
                name = "my district",
                externalId = "123",
                state = State(id = "FL", name = "Florida")
            ),
            accessExpiresOn = oldExpiryTime
        )
        val updatedExpiryTime = oldExpiryTime.plusDays(10)
        val updatedExpiryTimeToString = updatedExpiryTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME)
        val request = UpdateAccountRequest(accessExpiresOn = updatedExpiryTimeToString)
        val updatedOrganisation = updateAccount(district.id.value, request)

        assertThat(updatedOrganisation.accessExpiresOn).isEqualTo(updatedExpiryTime)
    }

    @Test
    fun `Throws an error when organisation to update cannot be found`() {
        assertThrows<OrganisationNotFoundException>{
            updateAccount("non-existent-org", UpdateAccountRequest(accessExpiresOn = "2019-06-06T00:00:00Z"))
        }
    }

    @Test
    fun `Throws an exception when the request contains an invalid date`() {
        assertThrows<InvalidDateException> {
            updateAccount(
                id = "organisation-id",
                request = UpdateAccountRequest("invalid-date")
            )
        }
    }
}
