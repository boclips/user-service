package com.boclips.users.application.commands

import com.boclips.users.application.model.OrganisationFilter
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.OrganisationDetailsFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class GetAccountsTest : AbstractSpringIntegrationTest(){

    @Autowired
    lateinit var getAccounts: GetAccounts

    @Test
    fun `Lists all accounts`() {
        accountRepository.save(
            school = OrganisationDetailsFactory.school(
                externalId = "org-id-1",
                name = "organisation 1",
                country = Country.fromCode("GBR")
            )
        )
        accountRepository.save(
            school = OrganisationDetailsFactory.school(
                externalId = "org-id-2",
                name = "organisation 2",
                country = Country.fromCode("USA"),
                state = State.fromCode("NY")
            )
        )
        accountRepository.save(
            district = OrganisationDetailsFactory.district(
                externalId = "org-id-3",
                name = "another one",
                state = State.fromCode("FL")
            )
        )

        val organisations = getAccounts(OrganisationFilter(countryCode = null, page = 0, size = 10))

        assertThat(organisations).hasSize(3)
        val organisationNames = organisations.content.map { it.organisation.name }
        assertThat(organisationNames).contains("organisation 1")
        assertThat(organisationNames).contains("organisation 2")
        assertThat(organisationNames).contains("another one")
    }

    @Test
    fun `searching non usa organisations`() {
        val ukSchool = accountRepository.save(
            school = OrganisationDetailsFactory.school(
                name = "organisation 1",
                country = Country.fromCode("GBR")
            )
        )
        accountRepository.save(
            school = OrganisationDetailsFactory.school(
                name = "organisation 2",
                country = Country.fromCode("USA"),
                state = State.fromCode("NY")
            )
        )
        accountRepository.save(
            district = OrganisationDetailsFactory.district(
                name = "another one",
                state = State.fromCode("FL")
            )
        )

        val searchRequest = OrganisationFilter(
            countryCode = "GBR",
            page = 0,
            size = 2
        )
//        val organisations = getAccounts(searchRequest)
//
//        assertThat1(organisations).hasSize(1)
//        assertThat1(organisations).containsExactly(ukSchool)
    }

}
