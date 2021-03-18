package com.boclips.users.application.commands

import com.boclips.security.testing.setSecurityContext
import com.boclips.users.api.request.UpdateOrganisationRequest
import com.boclips.users.application.exceptions.InvalidDateException
import com.boclips.users.application.exceptions.OrganisationNotFoundException
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.access.ContentPackageId
import com.boclips.users.domain.model.organisation.Address
import com.boclips.users.domain.model.organisation.ContentAccess
import com.boclips.users.domain.model.organisation.ExternalOrganisationId
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.school.State
import com.boclips.users.domain.service.UniqueId
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.OrganisationFactory
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class UpdateOrganisationTest : AbstractSpringIntegrationTest() {
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
    fun `Updates an organisation with a valid contentPackageId`() {

        val oldContentPackageId = ContentPackageId("12345")

        val organisation = organisationRepository.save(
            OrganisationFactory.apiIntegration(
                id = OrganisationId("5d43328744f0c2bd4574436a"),
                deal = OrganisationFactory.deal(
                    contentAccess = ContentAccess.SimpleAccess(oldContentPackageId)
                )
            )
        )

        val newContentPackageId = ContentPackageId("678910")
        val request = UpdateOrganisationRequest(contentPackageId = newContentPackageId.value)
        val updatedOrganisation = updateOrganisation(organisation.id.value, request)

        assertThat((updatedOrganisation.deal.contentAccess as? ContentAccess.SimpleAccess)?.id).isEqualTo(newContentPackageId)
    }

    @Test
    fun `Updates an organisation with a valid billing field`() {

        val oldBilling = false

        val organisation = organisationRepository.save(
            OrganisationFactory.apiIntegration(
                id = OrganisationId("5d43328744f0c2bd4574436a"),
                deal = OrganisationFactory.deal(
                    billing = oldBilling
                )
            )
        )

        val newBilling = true
        val request = UpdateOrganisationRequest(billing = newBilling)
        val updatedOrganisation = updateOrganisation(organisation.id.value, request)

        assertThat(updatedOrganisation.deal.billing).isEqualTo(newBilling)
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

    @Test
    fun `trims domain`() {
        val organisation = organisationRepository.save(OrganisationFactory.district())

        val updated = updateOrganisation.invoke(organisation.id.value, UpdateOrganisationRequest(" domain.com "))
        Assertions.assertThat(updated.domain).isEqualTo("domain.com")
    }
}
