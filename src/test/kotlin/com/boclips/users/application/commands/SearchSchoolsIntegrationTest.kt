package com.boclips.users.application.commands

import com.boclips.users.domain.model.OrganisationType
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class SearchSchoolsIntegrationTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var searchSchools: SearchSchools

    @Test
    fun `when country is not USA, looks up schools in the database`() {
        organisationRepository.save(organisationName = "school 1", organisationType = OrganisationType.School, country = "GBR")
        organisationRepository.save(organisationName = "school 2", organisationType = OrganisationType.School, country = "HUN")
        organisationRepository.save(organisationName = "another one", organisationType = OrganisationType.School, country = "GBR")

        val schools = searchSchools(school = "school", countryId = "GBR", state = null)

        assertThat(schools).hasSize(1)
        assertThat(schools[0].name).isEqualTo("school 1")
    }
}