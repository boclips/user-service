package com.boclips.users.presentation.resources

import com.boclips.security.testing.setSecurityContext
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.contentpackage.ContentPackageId
import com.boclips.users.domain.model.organisation.DealType
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.school.State
import com.boclips.users.presentation.hateoas.OrganisationLinkBuilder
import com.boclips.users.presentation.converters.OrganisationConverter
import com.boclips.users.presentation.converters.OrganisationDetailsConverter
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.OrganisationDetailsFactory
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.nhaarman.mockitokotlin2.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

class OrganisationConverterTest : AbstractSpringIntegrationTest() {
    lateinit var organisationLinkBuilder: OrganisationLinkBuilder
    lateinit var organisationDetailsConverter: OrganisationDetailsConverter
    lateinit var organisationConverter: OrganisationConverter

    @BeforeEach
    fun setUp() {
        organisationLinkBuilder = OrganisationLinkBuilder(mock())

        organisationConverter =
            OrganisationConverter(
                organisationLinkBuilder
            )
    }

    @Test
    fun toResource() {
        setSecurityContext("org-viewer", UserRoles.UPDATE_ORGANISATIONS)

        val originalOrganisation = OrganisationFactory.sample(
            id = OrganisationId("organisation-account-id"),
            accessExpiresOn = ZonedDateTime.parse("2019-12-04T15:11:59.531Z"),
            contentPackageId = ContentPackageId("content-package-id"),
            details = OrganisationDetailsFactory.district(name = "my-district", state = State.fromCode("NY")),
            type = DealType.DESIGN_PARTNER
        )

        val organisationResource = organisationConverter.toResource(originalOrganisation)

        assertThat(organisationResource.id).isEqualTo(originalOrganisation.id.value)
        assertThat(organisationResource.accessExpiresOn).isEqualTo(originalOrganisation.accessExpiresOn)
        assertThat(organisationResource.contentPackageId).isEqualTo(originalOrganisation.contentPackageId!!.value)
        assertThat(organisationResource.organisationDetails.name).isEqualTo(originalOrganisation.details.name)
        assertThat(organisationResource.organisationDetails.country?.name).isEqualTo(originalOrganisation.details.country?.name)
        assertThat(organisationResource.organisationDetails.state?.name).isEqualTo(originalOrganisation.details.state.name)
        assertThat(organisationResource.organisationDetails.type).isEqualTo(
            originalOrganisation.details.type().toString()
        )
        assertThat(organisationResource._links?.map { it.key }).containsExactlyInAnyOrder(
            "self",
            "edit",
            "associateUsers"
        )
    }
}
