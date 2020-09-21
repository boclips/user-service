package com.boclips.users.application.commands

import com.boclips.users.api.request.CreateDistrictRequest
import com.boclips.users.application.exceptions.OrganisationAlreadyExistsException
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.ContentPackageFactory
import com.boclips.users.testsupport.factories.OrganisationFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

class CreateDistrictTest : AbstractSpringIntegrationTest() {

    @Autowired
    lateinit var command: CreateDistrict

    @Test
    fun `should create new district with given name and content package`() {
        // given
        val name = "the_district_name"
        val contentPackage = ContentPackageFactory.sample()
        contentPackageRepository.save(contentPackage)

        val request = CreateDistrictRequest()
        request.name = name
        request.contentPackageId = contentPackage.id.value

        // when
        command(request)
        val district = organisationRepository.findDistrictByName(name)!!

        // then
        assertThat(district.name).isEqualTo(name)
        assertThat(district.deal.contentPackageId).isEqualTo(contentPackage.id)
    }

    @Test
    fun `should throw an exception when district of given name already exists`() {
        // given
        val name = "the_district_name"
        val contentPackage = ContentPackageFactory.sample()
        contentPackageRepository.save(contentPackage)

        organisationRepository.save(OrganisationFactory.district(name = name))

        val request = CreateDistrictRequest()
        request.name = name
        request.contentPackageId = contentPackage.id.value

        // then
        assertThrows<OrganisationAlreadyExistsException> { command(request) }
    }
}
