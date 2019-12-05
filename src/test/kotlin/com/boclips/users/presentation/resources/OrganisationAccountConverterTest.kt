package com.boclips.users.presentation.resources

import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.domain.model.organisation.OrganisationAccountId
import com.boclips.users.domain.model.organisation.OrganisationAccountType
import com.boclips.users.domain.model.school.State
import com.boclips.users.presentation.hateoas.OrganisationLinkBuilder
import com.boclips.users.presentation.resources.converters.OrganisationAccountConverter
import com.boclips.users.testsupport.factories.OrganisationAccountFactory
import com.boclips.users.testsupport.factories.OrganisationFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

internal class OrganisationAccountConverterTest {

    @Test
    fun toResource() {
        val originalOrganisation = OrganisationAccountFactory.sample(
            id = OrganisationAccountId("organisation-account-id"),
            accessExpiresOn = ZonedDateTime.parse("2019-12-04T15:11:59.531Z"),
            contractIds = listOf(ContractId("123")),
            organisation = OrganisationFactory.district(name = "my-district", state = State.fromCode("NY")),
            type = OrganisationAccountType.DESIGN_PARTNER
        )
        val organisationAccountResource = OrganisationAccountConverter(
            OrganisationLinkBuilder()
        ).toResource(originalOrganisation)

        assertThat(organisationAccountResource.content.id).isEqualTo(originalOrganisation.id.value)
        assertThat(organisationAccountResource.content.accessExpiresOn).isEqualTo(originalOrganisation.accessExpiresOn)
        assertThat(organisationAccountResource.content.organisation.name).isEqualTo(originalOrganisation.organisation.name)
        assertThat(organisationAccountResource.content.organisation.country?.name).isEqualTo(originalOrganisation.organisation.country?.name)
        assertThat(organisationAccountResource.content.organisation.state?.name).isEqualTo(originalOrganisation.organisation.state?.name)
        assertThat(organisationAccountResource.content.organisation.type).isEqualTo(originalOrganisation.organisation.type().toString())
        assertThat(organisationAccountResource.links).hasSize(2)
    }
}
