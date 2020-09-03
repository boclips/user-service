package com.boclips.users.domain.service.organisation

import com.boclips.users.domain.model.organisation.Deal
import com.boclips.users.domain.model.organisation.District
import com.boclips.users.domain.model.organisation.ExternalOrganisationId
import com.boclips.users.domain.model.organisation.ExternalOrganisationInformation
import com.boclips.users.domain.model.organisation.ExternalSchoolInformation
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.organisation.OrganisationRepository
import com.boclips.users.domain.model.organisation.School
import org.springframework.stereotype.Service

@Service
class OrganisationService(
    val americanSchoolsProvider: AmericanSchoolsProvider,
    val organisationRepository: OrganisationRepository
) {
    fun findOrCreateSchooldiggerSchool(externalSchoolId: ExternalOrganisationId): School? {
        val existingOrganisation = organisationRepository
            .findOrganisationByExternalId(externalSchoolId)

        if(existingOrganisation != null) {
            return existingOrganisation as School
        }

        val externalSchoolInfo = americanSchoolsProvider.fetchSchool(externalSchoolId.value) ?: return null

        return createSchool(externalSchoolInfo)
    }

    private fun getOrCreateDistrict(externalInfo: ExternalOrganisationInformation): District? {
        val existingOrganisation = organisationRepository.findOrganisationByExternalId(externalInfo.id)

        if(existingOrganisation != null) {
            return existingOrganisation as District
        }

        return createDistrict(externalInfo)
    }

    private fun createSchool(externalInfo: ExternalSchoolInformation): School? {
        return organisationRepository.save(
            School(
                id = OrganisationId(),
                name = externalInfo.school.name,
                address = externalInfo.school.address,
                deal = Deal(
                    contentPackageId = null,
                    billing = false,
                    accessExpiresOn = null
                ),
                role = null,
                tags = emptySet(),
                district = externalInfo.district?.let(this::getOrCreateDistrict),
                externalId = externalInfo.school.id,
                domain = null,
                features = null
            )
        )
    }

    private fun createDistrict(externalInfo: ExternalOrganisationInformation): District {
        return organisationRepository.save(
            District(
                id = OrganisationId(),
                name = externalInfo.name,
                address = externalInfo.address,
                deal = Deal(
                    contentPackageId = null,
                    billing = false,
                    accessExpiresOn = null
                ),
                tags = emptySet(),
                role = null,
                externalId = externalInfo.id,
                domain = null,
                features = null
            )
        )
    }
}
