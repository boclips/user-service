package com.boclips.users.application.commands

import com.boclips.users.domain.model.organisation.School
import com.boclips.users.domain.model.school.Country
import com.boclips.users.infrastructure.organisation.OrganisationType
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.OrganisationFactory
import io.opencensus.stats.Aggregation
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class SearchSchoolsIntegrationTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var searchSchools: SearchSchools

    @Test
    fun `when country is not USA, looks up schools in the database`() {
        organisationAccountRepository.save(organisation = OrganisationFactory.school(name = "school 1", country = Country.fromCode("GBR")))
        organisationAccountRepository.save(organisation = OrganisationFactory.school(name = "school 2", country = Country.fromCode("HUN")))
        organisationAccountRepository.save(organisation = OrganisationFactory.school(name = "another one", country = Country.fromCode("GBR")))

        val schools = searchSchools(school = "school", countryId = "GBR", state = null)

        assertThat(schools).hasSize(1)
        assertThat(schools[0].name).isEqualTo("school 1")
    }
}