package com.boclips.users.presentation.resources

import com.boclips.users.domain.model.account.AccountId
import com.boclips.users.domain.model.account.AccountType
import com.boclips.users.domain.model.accessrules.AccessRuleId
import com.boclips.users.domain.model.school.State
import com.boclips.users.presentation.hateoas.AccountLinkBuilder
import com.boclips.users.presentation.resources.converters.AccountConverter
import com.boclips.users.testsupport.factories.OrganisationAccountFactory
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.nhaarman.mockitokotlin2.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

class AccountConverterTest {
    @Test
    fun toResource() {
        val originalAccount = OrganisationAccountFactory.sample(
            id = AccountId("organisation-account-id"),
            accessExpiresOn = ZonedDateTime.parse("2019-12-04T15:11:59.531Z"),
            accessRuleIds = listOf(AccessRuleId("123")),
            organisation = OrganisationFactory.district(name = "my-district", state = State.fromCode("NY")),
            type = AccountType.DESIGN_PARTNER
        )
        val accountResource = AccountConverter(
            AccountLinkBuilder(mock())
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
