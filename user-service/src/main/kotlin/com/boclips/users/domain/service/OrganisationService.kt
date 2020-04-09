package com.boclips.users.domain.service

import com.boclips.users.domain.model.organisation.DealType
import com.boclips.users.domain.model.organisation.District
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.organisation.School
import org.springframework.stereotype.Service

@Service
class OrganisationService(
    val americanSchoolsProvider: AmericanSchoolsProvider,
    val organisationRepository: OrganisationRepository
) {
    fun findOrCreateSchooldiggerSchool(externalSchoolId: String): Organisation<School>? {
        var schoolOrganisation = organisationRepository.findOrganisationByExternalId(externalSchoolId)
            ?.takeIf { it.details is School }
            ?.let {
                @Suppress("UNCHECKED_CAST")
                it as Organisation<School>
            }

        if (schoolOrganisation == null) {
            val (school, district) = americanSchoolsProvider.fetchSchool(externalSchoolId) ?: null to null
            schoolOrganisation = school
                ?.copy(district = district?.let { getOrCreateDistrict(district) })
                ?.let {
                    val organisation = Organisation(
                        id = OrganisationId(),
                        details = it,
                        accessExpiresOn = null,
                        type = DealType.STANDARD,
                        role = null
                    )

                    organisationRepository.save(organisation)
                }
        }

        return schoolOrganisation
    }

    private fun getOrCreateDistrict(district: District): Organisation<District>? {
        return organisationRepository.findOrganisationByExternalId(district.externalId)
            ?.takeIf { it.details is District }
            ?.let {
                @Suppress("UNCHECKED_CAST")
                it as Organisation<District>
            }
            ?: organisationRepository.save(
                Organisation(
                    id = OrganisationId(),
                    details = district,
                    accessExpiresOn = null,
                    type = DealType.STANDARD,
                    role = null
                )
            )
    }
}
