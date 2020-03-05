package com.boclips.users.domain.service

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.OrganisationDetailsFactory
import com.boclips.users.testsupport.factories.OrganisationFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class OrganisationServiceTest : AbstractSpringIntegrationTest() {

    @Test
    fun `when school and district already exists delegates on DB`() {
        val district = organisationRepository.save(
            OrganisationFactory.sample(details = OrganisationDetailsFactory.district())
        )

        val originalSchool = OrganisationDetailsFactory.school(externalId = "external-school-id", district = district)
        organisationRepository.save(OrganisationFactory.sample(details = originalSchool))

        val school = organisationService.findOrCreateSchooldiggerSchool("external-school-id")

        assertThat(school?.details).isEqualTo(originalSchool)
        assertThat(fakeAmericanSchoolsProvider.callCount()).isEqualTo(0)
    }

    @Test
    fun `existing district new school searches them first time only and links both`() {
        val district = OrganisationDetailsFactory.district(externalId = "external-district-id")
        val districtAccount = organisationRepository.save(OrganisationFactory.sample(details = district))
        val expectedSchool = OrganisationDetailsFactory.school(
            externalId = "external-school-id",
            district = districtAccount
        )
        fakeAmericanSchoolsProvider.createSchoolAndDistrict(expectedSchool.copy(district = null) to district)

        val schoolAccount = organisationService.findOrCreateSchooldiggerSchool("external-school-id")!!

        assertThat(schoolAccount).isEqualTo(organisationService.findOrCreateSchooldiggerSchool("external-school-id"))
        assertThat(schoolAccount.details).isEqualTo(expectedSchool)
        assertThat(schoolAccount.details.district).isEqualTo(districtAccount)
        assertThat(fakeAmericanSchoolsProvider.callCount()).isEqualTo(1)
    }

    @Test
    fun `when neither school nor district exists creates both`() {
        val district = OrganisationDetailsFactory.district(externalId = "external-district-id")
        val school = OrganisationDetailsFactory.school(externalId = "external-school-id")
        fakeAmericanSchoolsProvider.createSchoolAndDistrict(school to district)

        val schoolAccount = organisationService.findOrCreateSchooldiggerSchool("external-school-id")!!

        assertThat(schoolAccount).isEqualTo(organisationService.findOrCreateSchooldiggerSchool("external-school-id"))
        assertThat(schoolAccount.details.externalId).isEqualTo("external-school-id")
        assertThat(schoolAccount.details.district?.details).isEqualTo(district)
        assertThat(fakeAmericanSchoolsProvider.callCount()).isEqualTo(1)
    }

    @Test
    fun `when american school provider cannot find school returns null`() {
        fakeAmericanSchoolsProvider.createSchoolAndDistrict(null)

        val schoolAccount = organisationService.findOrCreateSchooldiggerSchool("external-school-id")

        assertThat(schoolAccount).isNull()
    }
}
