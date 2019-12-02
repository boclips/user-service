package com.boclips.users.domain.service

import com.boclips.users.domain.model.organisation.District
import com.boclips.users.domain.model.organisation.OrganisationAccount
import com.boclips.users.domain.model.organisation.School
import com.boclips.users.domain.model.school.Country
import org.springframework.stereotype.Service

@Service
class OrganisationService(
    val americanSchoolsProvider: AmericanSchoolsProvider,
    val organisationAccountRepository: OrganisationAccountRepository
) {
    fun findOrCreateSchooldiggerSchool(externalSchoolId: String): OrganisationAccount<School>? {
        var schoolAccount = organisationAccountRepository.findOrganisationAccountByExternalId(externalSchoolId)
            ?.takeIf { it.organisation is School }
            ?.let {
                @Suppress("UNCHECKED_CAST")
                it as OrganisationAccount<School>
            }

        if (schoolAccount == null) {
            val (school, district) = americanSchoolsProvider.fetchSchool(externalSchoolId) ?: null to null
            schoolAccount = school
                ?.copy(district = district?.let { getOrCreateDistrict(district) })
                ?.let { organisationAccountRepository.save(it) }
        }

        return schoolAccount
    }

    private fun getOrCreateDistrict(district: District): OrganisationAccount<District>? {
        return organisationAccountRepository.findOrganisationAccountByExternalId(district.externalId)
            ?.takeIf { it.organisation is District }
            ?.let {
                @Suppress("UNCHECKED_CAST")
                it as OrganisationAccount<District>
            }
            ?: organisationAccountRepository.save(district)
    }
}
