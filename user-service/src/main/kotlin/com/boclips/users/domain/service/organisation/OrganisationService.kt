package com.boclips.users.domain.service.organisation

import com.boclips.users.domain.model.organisation.Address
import com.boclips.users.domain.model.organisation.Deal
import com.boclips.users.domain.model.organisation.District
import com.boclips.users.domain.model.organisation.ExternalOrganisationId
import com.boclips.users.domain.model.organisation.ExternalOrganisationInformation
import com.boclips.users.domain.model.organisation.ExternalSchoolInformation
import com.boclips.users.domain.model.organisation.LtiDeployment
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.organisation.OrganisationRepository
import com.boclips.users.domain.model.organisation.School
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class OrganisationService(
    val americanSchoolsProvider: AmericanSchoolsProvider,
    val organisationRepository: OrganisationRepository
) {
    companion object : KLogging()

    fun findOrCreateSchooldiggerSchool(externalSchoolId: ExternalOrganisationId): School? {
        val existingOrganisation = organisationRepository
            .findOrganisationByExternalId(externalSchoolId)

        if (existingOrganisation != null) {
            return existingOrganisation as School
        }

        val externalSchoolInfo = americanSchoolsProvider.fetchSchool(externalSchoolId.value) ?: return null

        return createSchool(externalSchoolInfo)
    }

    fun findOrCreateLtiDeployment(topLevelOrganisationId: OrganisationId, deploymentId: String): Organisation {
        return organisationRepository.findOrganisationsByParentId(topLevelOrganisationId)
            .find { organisation -> (organisation as LtiDeployment).deploymentId == deploymentId }
            ?: saveDeploymentOrganisation(deploymentId, topLevelOrganisationId)
    }

    private fun getOrCreateDistrict(externalInfo: ExternalOrganisationInformation): District? {
        val existingOrganisation = organisationRepository.findOrganisationByExternalId(externalInfo.id)

        if (existingOrganisation != null) {
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
                    billing = false,
                    accessExpiresOn = null
                ),
                role = null,
                tags = emptySet(),
                district = externalInfo.district?.let(this::getOrCreateDistrict),
                externalId = externalInfo.school.id,
                domain = null,
                features = null,
                logoUrl = null,
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
                    billing = false,
                    accessExpiresOn = null
                ),
                tags = emptySet(),
                role = null,
                externalId = externalInfo.id,
                domain = null,
                features = null,
                logoUrl = null,
            )
        )
    }

    private fun saveDeploymentOrganisation(deploymentId: String, topLevelOrganisationId: OrganisationId): Organisation {
        logger.info { "creating deployment organisation: deploymentId:$deploymentId, topLevelOrganisationId: ${topLevelOrganisationId.value}" }
        val integrationOrganisation = organisationRepository.findOrganisationById(topLevelOrganisationId)!!
        val organisation = LtiDeployment(
            id = OrganisationId(),
            name = deploymentId,
            address = Address(),
            deal = Deal(
                billing = false,
                accessExpiresOn = null
            ),
            tags = emptySet(),
            role = "LTI_DEPLOYMENT",
            domain = null,
            features = null,
            deploymentId = deploymentId,
            parent = integrationOrganisation,
            logoUrl = null,
        )
        return organisationRepository.save(organisation)
    }
}
