package com.boclips.users.domain.service

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.OrganisationFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class OrganisationServiceTest: AbstractSpringIntegrationTest() {

    @Autowired
    lateinit var organisationService: OrganisationService

    @Test
    fun `when school already exists within district returns existing school`() {
        val district = organisationAccountRepository.save(OrganisationFactory.district())
        val originalSchool = OrganisationFactory.school(externalId = "external-school-id", district = district)
        organisationAccountRepository.save(originalSchool)

        val school = organisationService.findOrCreateAmericanSchool("external-school-id")

        assertThat(school).isEqualTo(originalSchool)
        assertThat(fakeAmericanSchoolsProvider.callCount()).isEqualTo(0)
    }

    @Test
    fun `when school does not exist but district exists attaches school to district`() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @Test
    fun `when neither school nor district exists creates both`() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @Test
    fun `when american school privider cannot find school returns null`() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}