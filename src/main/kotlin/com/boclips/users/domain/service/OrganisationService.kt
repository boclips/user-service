package com.boclips.users.domain.service

import com.boclips.users.domain.model.organisation.District
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.School
import org.springframework.stereotype.Service

@Service
class OrganisationService(
    val americanSchoolsProvider: AmericanSchoolsProvider,
    val organisationRepository: OrganisationRepository
) {
    fun findOrCreateSchooldiggerSchool(externalSchoolId: String): Organisation<School>? {
        var schoolAccount = organisationRepository.findOrganisationByExternalId(externalSchoolId)
            ?.takeIf { it.organisation is School }
            ?.let {
                @Suppress("UNCHECKED_CAST")
                it as Organisation<School>
            }

        if (schoolAccount == null) {
            val (school, district) = americanSchoolsProvider.fetchSchool(externalSchoolId) ?: null to null
            schoolAccount = school
                ?.copy(district = district?.let { getOrCreateDistrict(district) })
                ?.let { organisationRepository.save(it) }
        }

        return schoolAccount
    }

    private fun getOrCreateDistrict(district: District): Organisation<District>? {
        return organisationRepository.findOrganisationByExternalId(district.externalId)
            ?.takeIf { it.organisation is District }
            ?.let {
                @Suppress("UNCHECKED_CAST")
                it as Organisation<District>
            }
            ?: organisationRepository.save(district)
    }
}
