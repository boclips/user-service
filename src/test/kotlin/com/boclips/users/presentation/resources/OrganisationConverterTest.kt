package com.boclips.users.presentation.resources

import com.boclips.users.domain.model.contentpackage.AccessRuleId
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.organisation.DealType
import com.boclips.users.domain.model.school.State
import com.boclips.users.presentation.hateoas.OrganisationLinkBuilder
import com.boclips.users.presentation.resources.converters.AccountConverter
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.users.testsupport.factories.OrganisationDetailsFactory
import com.nhaarman.mockitokotlin2.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

class OrganisationConverterTest {
    @Test
    fun toResource() {
        val originalAccount = OrganisationFactory.sample(
            id = OrganisationId("organisation-account-id"),
            accessExpiresOn = ZonedDateTime.parse("2019-12-04T15:11:59.531Z"),
            accessRuleIds = listOf(AccessRuleId("123")),
            organisationDetails = OrganisationDetailsFactory.district(name = "my-district", state = State.fromCode("NY")),
            type = DealType.DESIGN_PARTNER
        )
        val accountResource = AccountConverter(
            OrganisationLinkBuilder(mock())
        ).toResource(originalAccount)

        assertThat(accountResource.content!!.id).isEqualTo(originalAccount.id.value)
        assertThat(accountResource.content!!.accessExpiresOn).isEqualTo(originalAccount.accessExpiresOn)
        assertThat(accountResource.content!!.organisation.name).isEqualTo(originalAccount.organisation.name)
        assertThat(accountResource.content!!.organisation.country?.name).isEqualTo(originalAccount.organisation.country?.name)
        assertThat(accountResource.content!!.organisation.state?.name).isEqualTo(originalAccount.organisation.state?.name)
        assertThat(accountResource.content!!.organisation.type).isEqualTo(originalAccount.organisation.type().toString())
        assertThat(accountResource.links.map { it.rel.value() })
            .containsExactlyInAnyOrder("self", "edit")
    }
}
