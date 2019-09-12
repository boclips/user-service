package com.boclips.users.application.commands

import com.boclips.users.domain.model.LookupEntry
import com.boclips.users.domain.model.school.Country
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.OrganisationFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class SearchSchoolsIntegrationTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var searchSchools: SearchSchools

    @Test
    fun `when school is not from USA`() {
        organisationAccountRepository.save(
            school = OrganisationFactory.school(
                name = "school 1",
                country = Country.fromCode("GBR")
            )
        )
        organisationAccountRepository.save(
            school = OrganisationFactory.school(
                name = "school 2",
                country = Country.fromCode("HUN")
            )
        )
        organisationAccountRepository.save(
            school = OrganisationFactory.school(
                name = "another one",
                country = Country.fromCode("GBR")
            )
        )

        val schools = searchSchools(schoolName = "school", countryCode = "GBR", state = null)

        assertThat(schools).hasSize(1)
        assertThat(schools[0].name).isEqualTo("school 1")
    }

    @Test
    fun `when school is from the USA fetches them from external API only`() {
        organisationAccountRepository.save(
            school = OrganisationFactory.school(
                name = "school 1",
                country = Country.fromCode("USA")
            )
        )
        fakeAmericanSchoolsProvider.createLookupEntries(
            "NY",
            LookupEntry("", "usa school 1"),
            LookupEntry("", "usa school 2"),
            LookupEntry("", "usa hot dog shop 1")
        )

        val schools = searchSchools(schoolName = "school", countryCode = "USA", state = "NY")

        assertThat(schools).hasSize(2)
        assertThat(schools.map { it.name }).containsExactlyInAnyOrder("usa school 1", "usa school 2")
    }
}