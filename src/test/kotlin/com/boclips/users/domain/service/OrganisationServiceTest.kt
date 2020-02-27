package com.boclips.users.domain.service

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.OrganisationDetailsFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class OrganisationServiceTest: AbstractSpringIntegrationTest() {

    @Test
    fun `when school and district already exists delegates on DB`() {
        val district = accountRepository.save(OrganisationDetailsFactory.district())
        val originalSchool = OrganisationDetailsFactory.school(externalId = "external-school-id", district = district)
        accountRepository.save(originalSchool)

        val school = accountService.findOrCreateSchooldiggerSchool("external-school-id")

        assertThat(school?.organisation).isEqualTo(originalSchool)
        assertThat(fakeAmericanSchoolsProvider.callCount()).isEqualTo(0)
    }

    @Test
    fun `existing district new school searches them first time only and links both`() {
        val district = OrganisationDetailsFactory.district(externalId = "external-district-id")
        val districtAccount = accountRepository.save(district)
        val expectedSchool = OrganisationDetailsFactory.school(
                externalId = "external-school-id",
                district = districtAccount
        )
        fakeAmericanSchoolsProvider.createSchoolAndDistrict(expectedSchool.copy(district = null) to district)

        val schoolAccount = accountService.findOrCreateSchooldiggerSchool("external-school-id")!!

        assertThat(schoolAccount).isEqualTo(accountService.findOrCreateSchooldiggerSchool("external-school-id"))
        assertThat(schoolAccount.organisation).isEqualTo(expectedSchool)
        assertThat(schoolAccount.organisation.district).isEqualTo(districtAccount)
        assertThat(fakeAmericanSchoolsProvider.callCount()).isEqualTo(1)
    }

    @Test
    fun `when neither school nor district exists creates both`() {
        val district = OrganisationDetailsFactory.district(externalId = "external-district-id")
        val school = OrganisationDetailsFactory.school(externalId = "external-school-id")
        fakeAmericanSchoolsProvider.createSchoolAndDistrict(school to district)

        val schoolAccount = accountService.findOrCreateSchooldiggerSchool("external-school-id")!!

        assertThat(schoolAccount).isEqualTo(accountService.findOrCreateSchooldiggerSchool("external-school-id"))
        assertThat(schoolAccount.organisation.externalId).isEqualTo("external-school-id")
        assertThat(schoolAccount.organisation.district?.organisation).isEqualTo(district)
        assertThat(fakeAmericanSchoolsProvider.callCount()).isEqualTo(1)
    }

    @Test
    fun `when american school provider cannot find school returns null`() {
        fakeAmericanSchoolsProvider.createSchoolAndDistrict(null)

        val schoolAccount = accountService.findOrCreateSchooldiggerSchool("external-school-id")

        assertThat(schoolAccount).isNull()
    }
}
