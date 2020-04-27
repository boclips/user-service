package com.boclips.users.domain.service

import com.boclips.users.domain.model.organisation.Address
import com.boclips.users.domain.model.organisation.ExternalOrganisationId
import com.boclips.users.domain.model.organisation.ExternalOrganisationInformation
import com.boclips.users.domain.model.organisation.ExternalSchoolInformation
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.users.testsupport.factories.OrganisationFactory.Companion.district
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class OrganisationServiceTest : AbstractSpringIntegrationTest() {

    @Test
    fun `when school and district already exists delegates on DB`() {
        val district = organisationRepository.save(district())

        val originalSchool = OrganisationFactory.school(
            externalId = ExternalOrganisationId("external-school-id"),
            district = district
        )
        organisationRepository.save(originalSchool)

        val school = organisationService.findOrCreateSchooldiggerSchool(ExternalOrganisationId("external-school-id"))

        assertThat(school).isEqualTo(originalSchool)
        assertThat(fakeAmericanSchoolsProvider.callCount()).isEqualTo(0)
    }

    @Test
    fun `existing district new school searches them first time only and links both`() {
        val district = organisationRepository.save(district(externalId = ExternalOrganisationId("external-district-id")))
        fakeAmericanSchoolsProvider.createSchoolAndDistrict(
            ExternalSchoolInformation(
                school = ExternalOrganisationInformation(
                    id = ExternalOrganisationId("external-school-id"),
                    name = "School name",
                    address = Address()
                ),
                district = ExternalOrganisationInformation(
                    id = ExternalOrganisationId("external-district-id"),
                    name = "District name",
                    address = Address()
                )
            )
        )

        val schoolAccount = organisationService.findOrCreateSchooldiggerSchool(ExternalOrganisationId("external-school-id"))!!

        assertThat(schoolAccount.externalId?.value).isEqualTo("external-school-id")
        assertThat(schoolAccount.district).isEqualTo(district)
        assertThat(fakeAmericanSchoolsProvider.callCount()).isEqualTo(1)
    }

    @Test
    fun `when neither school nor district exists creates both`() {
        fakeAmericanSchoolsProvider.createSchoolAndDistrict(
            ExternalSchoolInformation(
                school = ExternalOrganisationInformation(
                    id = ExternalOrganisationId("external-school-id"),
                    name = "School name",
                    address = Address()
                ),
                district = ExternalOrganisationInformation(
                    id = ExternalOrganisationId("external-district-id"),
                    name = "District name",
                    address = Address()
                )
            )
        )

        val schoolAccount = organisationService.findOrCreateSchooldiggerSchool(ExternalOrganisationId("external-school-id"))!!

        assertThat(schoolAccount.externalId?.value).isEqualTo("external-school-id")
        assertThat(schoolAccount.district?.externalId?.value).isEqualTo("external-district-id")
        assertThat(fakeAmericanSchoolsProvider.callCount()).isEqualTo(1)
    }

    @Test
    fun `when american school provider cannot find school returns null`() {
        fakeAmericanSchoolsProvider.createSchoolAndDistrict(null)

        val schoolAccount = organisationService.findOrCreateSchooldiggerSchool(ExternalOrganisationId("external-school-id"))

        assertThat(schoolAccount).isNull()
    }
}
